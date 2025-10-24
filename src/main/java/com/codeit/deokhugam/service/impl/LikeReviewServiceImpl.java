package com.codeit.deokhugam.service.impl;

import com.codeit.deokhugam.common.exception.AuthorizationException;
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
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LikeReviewServiceImpl implements LikeReviewService {

  private final ReviewLikeRepository reviewLikeRepository;
  private final ReviewRepository reviewRepository;
  private final MemberRepository memberRepository;

  @Override
  @Transactional
  public LikeReviewResult likeReview(LikeReviewCommand command) {
    long memberId = command.getMemberId();
    long reviewId = command.getReviewId();

    Review review = reviewRepository.findByIdAndDeletedIsFalse(reviewId).orElseThrow(()
        -> new ResourceNotFoundException("review with ID" + reviewId + " not found", reviewId));
    Member member = memberRepository.findByIdAndDeletedIsFalse(memberId).orElseThrow(()
        -> new ResourceNotFoundException("member with ID" + memberId + " not found", memberId));

    if (review.getMember().getId() != memberId) {
      throw new AuthorizationException("허용되지 않은 연산입니다.");
    }

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
}
