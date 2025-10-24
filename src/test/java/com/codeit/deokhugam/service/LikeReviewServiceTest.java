package com.codeit.deokhugam.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;

import com.codeit.deokhugam.common.exception.AuthorizationException;
import com.codeit.deokhugam.common.exception.ResourceNotFoundException;
import com.codeit.deokhugam.domain.entity.Member;
import com.codeit.deokhugam.domain.entity.Review;
import com.codeit.deokhugam.domain.entity.ReviewLike;
import com.codeit.deokhugam.repository.MemberRepository;
import com.codeit.deokhugam.repository.ReviewLikeRepository;
import com.codeit.deokhugam.repository.ReviewRepository;
import com.codeit.deokhugam.service.impl.LikeReviewServiceImpl;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class LikeReviewServiceTest {

  @InjectMocks
  private LikeReviewServiceImpl likeReviewService;
  @Mock
  private ReviewLikeRepository reviewLikeRepository;
  @Mock
  private ReviewRepository reviewRepository;
  @Mock
  private MemberRepository memberReviewRepository;

  LikeReviewCommand command;
  Member member;
  Review review;
  ReviewLike reviewLike;

  @BeforeEach
  void setup() {
    member = Member.builder().id(1L).build();
    review = Review.builder()
        .id(1L)
        .member(member)
        .likeCount(0L)
        .build();
    reviewLike = new ReviewLike(review, member);

    command = LikeReviewCommand.builder()
        .memberId(member.getId())
        .reviewId(review.getId())
        .build();
  }

  @Test
  @DisplayName("리뷰 좋아요 테스트 - 종아요")
  void LikeReviewWithLike() {
    // Given
    given(reviewRepository.findByIdAndDeletedIsFalse(any())).willReturn(Optional.of(review));
    given(memberReviewRepository.findByIdAndDeletedIsFalse(any())).willReturn(Optional.of(member));
    given(reviewLikeRepository.findByMemberIdAndReviewId(any(), any()))
        .willReturn(Optional.empty());
    given(reviewLikeRepository.save(any())).willReturn(reviewLike);

    // When
    LikeReviewResult result = likeReviewService.likeReview(command);

    // Then
    assertThat(result.getReviewId()).isEqualTo(review.getId());
    assertThat(result.getMemberId()).isEqualTo(member.getId());
    assertThat(result.isLiked()).isTrue();
  }

  @Test
  @DisplayName("리뷰 좋아요 테스트 - 종아요 취소")
  void LikeReviewWithUnlike() {
    // Given
    given(reviewRepository.findByIdAndDeletedIsFalse(any())).willReturn(Optional.of(review));
    given(memberReviewRepository.findByIdAndDeletedIsFalse(any())).willReturn(Optional.of(member));
    given(reviewLikeRepository.findByMemberIdAndReviewId(any(), any()))
        .willReturn(Optional.of(reviewLike));
    doNothing().when(reviewLikeRepository).deleteById(any());

    // When
    LikeReviewResult result = likeReviewService.likeReview(command);

    // Then
    assertThat(result.getReviewId()).isEqualTo(review.getId());
    assertThat(result.getMemberId()).isEqualTo(member.getId());
    assertThat(result.isLiked()).isFalse();

  }

  @Test
  @DisplayName("리뷰 좋아요 테스트 - 권한이 없을 때")
  void LikeReviewWithNotAllowed() {
    // Given
    given(reviewRepository.findByIdAndDeletedIsFalse(any())).willReturn(Optional.of(review));
    given(memberReviewRepository.findByIdAndDeletedIsFalse(any())).willReturn(Optional.of(member));

    long wrongId = 999L;
    LikeReviewCommand notAllowedCommand = LikeReviewCommand.builder()
        .memberId(wrongId)
        .reviewId(review.getId())
        .build();
    // When Then
    assertThatThrownBy(() -> likeReviewService.likeReview(notAllowedCommand))
        .isInstanceOf(AuthorizationException.class);
  }

  @Test
  @DisplayName("리뷰 좋아요 테스트 - 리뷰를 찾을 수 없을 때")
  void LikeReviewWithNotFoundReview() {
    // Given
    given(reviewRepository.findByIdAndDeletedIsFalse(any())).willReturn(Optional.empty());

    // When Then
    assertThatThrownBy(() -> likeReviewService.likeReview(command))
        .isInstanceOf(ResourceNotFoundException.class);


  }


}
