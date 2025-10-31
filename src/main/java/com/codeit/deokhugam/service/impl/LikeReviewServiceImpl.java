package com.codeit.deokhugam.service.impl;

import com.codeit.deokhugam.cache.DebounceCache;
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
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
public class LikeReviewServiceImpl implements LikeReviewService {

  private final ReviewLikeRepository reviewLikeRepository;
  private final ReviewRepository reviewRepository;
  private final MemberRepository memberRepository;
  private final NotificationService notificationService;
  private final DebounceCache debounceCache;

  private final long ttlMs;

  public LikeReviewServiceImpl(ReviewLikeRepository reviewLikeRepository,
      ReviewRepository reviewRepository, MemberRepository memberRepository,
      NotificationService notificationService, DebounceCache debounceCache, Long ttlMs) {
    this.reviewLikeRepository = reviewLikeRepository;
    this.reviewRepository = reviewRepository;
    this.memberRepository = memberRepository;
    this.notificationService = notificationService;
    this.debounceCache = debounceCache;
    this.ttlMs = ttlMs;
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
      reviewLikeRepository.save(new ReviewLike(review, member));
      review.setLikeCount(review.getLikeCount() + 1);
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
  public LikeReviewResult likeReviewV2(LikeReviewCommand command) {
    long memberId = command.getMemberId();
    long reviewId = command.getReviewId();

    Review review = reviewRepository.findByIdAndDeletedIsFalse(reviewId).orElseThrow(()
        -> new ResourceNotFoundException("review with ID" + reviewId + " not found", reviewId));
    Member member = memberRepository.findByIdAndDeletedIsFalse(memberId).orElseThrow(()
        -> new ResourceNotFoundException("member with ID" + memberId + " not found", memberId));

    ReviewLike reviewLike = reviewLikeRepository
        .findByMemberIdAndReviewId(memberId, reviewId).orElse(null);
    boolean liked = (reviewLike == null);

    if (!liked) { // 좋아요 취소 -> count down  + 엔티티 제거
      reviewLikeRepository.deleteById(reviewLike.getId());
      review.setLikeCount(review.getLikeCount() - 1);
      return buildResult(reviewId, memberId, false);
    }
    reviewLikeRepository.save(new ReviewLike(review, member));
    review.setLikeCount(review.getLikeCount() + 1);

    // 좋아요 -> count up  + 엔티티 추가
    String debounceCacheKey = memberId + "_" + reviewId;
    boolean isCached = debounceCache.tryAcquire(debounceCacheKey, ttlMs);
    if (isCached) {
      notificationService.createLikeNotification(review.getMember(), review, member);
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
