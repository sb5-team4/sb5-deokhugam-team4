package com.codeit.deokhugam.service.impl;

import static com.codeit.deokhugam.common.exception.handler.ErrorCode.NOTIFICATION_SEND_FAILED;

import com.codeit.deokhugam.cache.ThrottleCache;
import com.codeit.deokhugam.common.exception.ResourceNotFoundException;
import com.codeit.deokhugam.domain.entity.Member;
import com.codeit.deokhugam.domain.entity.Review;
import com.codeit.deokhugam.domain.entity.ReviewLike;
import com.codeit.deokhugam.repository.MemberRepository;
import com.codeit.deokhugam.repository.ReviewLikeRepository;
import com.codeit.deokhugam.repository.ReviewRepository;
import com.codeit.deokhugam.service.LikeReviewCommand;
import com.codeit.deokhugam.service.LikeReviewResult;
import com.codeit.deokhugam.service.LikeReviewService;
import com.codeit.deokhugam.service.NotificationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class LikeReviewServiceImpl implements LikeReviewService {

  private static final Logger log = LoggerFactory.getLogger(LikeReviewServiceImpl.class);
  private final ReviewLikeRepository reviewLikeRepository;
  private final ReviewRepository reviewRepository;
  private final MemberRepository memberRepository;
  private final NotificationService notificationService;
  private final ThrottleCache throttleCache;


  @Value("${review.like.throttle.ttl-ms:60000}") // 1분 기본값
  private long reviewLikeTTL;

  public LikeReviewServiceImpl(ReviewLikeRepository reviewLikeRepository,
      ReviewRepository reviewRepository, MemberRepository memberRepository,
      NotificationService notificationService, ThrottleCache throttleCache) {
    this.reviewLikeRepository = reviewLikeRepository;
    this.reviewRepository = reviewRepository;
    this.memberRepository = memberRepository;
    this.notificationService = notificationService;
    this.throttleCache = throttleCache;
  }

  @Override
  @Transactional
  public LikeReviewResult likeReview(LikeReviewCommand command) {
    long memberId = command.getMemberId();
    long reviewId = command.getReviewId();

    Review review = reviewRepository.findByIdAndDeletedIsFalse(reviewId).orElseThrow(()
        -> new ResourceNotFoundException("review with ID" + reviewId + " not found", reviewId));
    Member member = memberRepository.findByIdAndDeletedIsFalse(memberId).orElseThrow(()
        -> new ResourceNotFoundException("member with ID" + memberId + " not found", memberId));

    ReviewLike reviewLike = reviewLikeRepository
        .findByMemberIdAndReviewId(memberId, reviewId).orElse(null);
    boolean liked = reviewLike == null;
    // 좋아요 -> count up  + 엔티티 추가
    // 좋아요 취소 -> count down  + 엔티티 제거
    if (liked) {
      // 1. Race Condition 으로 좋아요 중복 발생 시 무시 처리
      reviewLikeRepository.insertIgnoreConflict(reviewId, memberId);
      review.setLikeCount(review.getLikeCount() + 1);

      // 2. 알람 발송 실패 시 무시 처리
      try {
        notificationService.createLikeNotification(review.getMember(), review, member);

      } catch (Exception e) { // 알람 실패여도 롤백을 하지 않음
        log.warn(NOTIFICATION_SEND_FAILED.getMessage(), e);
      }
    } else {
      reviewLikeRepository.deleteById(reviewLike.getId());
      review.setLikeCount(review.getLikeCount() - 1);

    }

    return LikeReviewResult.builder()
        .reviewId(reviewId)
        .memberId(memberId)
        .liked(liked)
        .build();
  }

  /**
   * 알림 중복 문제를 해결하기 위해 V2 서비스
   *
   * @param command
   * @return
   */
  @Override
  @Transactional
  @Retryable(
      retryFor = {ObjectOptimisticLockingFailureException.class},
      maxAttempts = 10,
      backoff = @Backoff(100)
  )
  public LikeReviewResult likeReviewV2(LikeReviewCommand command) {
    long memberId = command.getMemberId();
    long reviewId = command.getReviewId();

    Review review = reviewRepository.findByIdAndDeletedIsFalse(reviewId).orElseThrow(()
        -> new ResourceNotFoundException("review with ID" + reviewId + " not found", reviewId));
    Member liker = memberRepository.findByIdAndDeletedIsFalse(memberId).orElseThrow(()
        -> new ResourceNotFoundException("member with ID" + memberId + " not found", memberId));
    ReviewLike reviewLike = reviewLikeRepository
        .findByMemberIdAndReviewId(memberId, reviewId).orElse(null);

    // 1. 종아요 취소일 경우 Return
    boolean liked = (reviewLike == null);
    if (!liked) { // 좋아요 취소 -> count down  + 엔티티 제거
      reviewLikeRepository.deleteById(reviewLike.getId());
      review.setLikeCount(review.getLikeCount() - 1);
      return buildResult(reviewId, memberId, false);
    }

    reviewLikeRepository.insertIgnoreConflict(reviewId, memberId);
    review.setLikeCount(review.getLikeCount() + 1);

    // 2. 작성자 본인의 좋아요일 경우 return
    Member author = review.getMember();
    boolean isAuthor = author.getId().equals(memberId);
    if (isAuthor) {
      return buildResult(reviewId, memberId, true);
    }

    // 3. Notification 처리
    String debounceCacheKey = memberId + "_" + reviewId;
    boolean isCached = throttleCache.tryAcquire(debounceCacheKey, reviewLikeTTL);
    if (isCached) {
      try {
        notificationService.createLikeNotification(author, review, liker);

      } catch (Exception e) { // 알람 실패여도 롤백을 하지 않음
        log.warn(NOTIFICATION_SEND_FAILED.getMessage(), e);
      }
    }

    return buildResult(reviewId, memberId, true);

  }

  private LikeReviewResult buildResult(Long reviewId, Long memberId, boolean liked) {
    return LikeReviewResult.builder()
        .reviewId(reviewId)
        .memberId(memberId)
        .liked(liked)
        .build();
  }
}
