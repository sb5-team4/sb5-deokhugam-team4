package com.codeit.deokhugam.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

import com.codeit.deokhugam.common.exception.AuthorizationException;
import com.codeit.deokhugam.domain.entity.Book;
import com.codeit.deokhugam.domain.entity.Member;
import com.codeit.deokhugam.domain.entity.Review;
import com.codeit.deokhugam.domain.entity.ReviewLike;
import com.codeit.deokhugam.dto.command.PatchReviewCommand;
import com.codeit.deokhugam.dto.result.PatchReviewResult;
import com.codeit.deokhugam.repository.MemberRepository;
import com.codeit.deokhugam.repository.ReviewLikeRepository;
import com.codeit.deokhugam.repository.ReviewRepository;
import com.codeit.deokhugam.repository.book.BookRepository;
import com.codeit.deokhugam.service.impl.ReviewServiceImpl;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class PatchReviewServiceTest {

  @InjectMocks
  private ReviewServiceImpl reviewService;
  @Mock
  ReviewRepository reviewRepository;
  @Mock
  BookRepository bookRepository;
  @Mock
  MemberRepository memberRepository;
  @Mock
  ReviewLikeRepository reviewLikeRepository;

  Book book;
  Member member;
  Member notAllowedMember;

  String title;
  String author;
  String description;
  String publisher;
  Review review;
  boolean deleted;
  Short rating;
  Short newRating;
  String content;
  String newContent;
  PatchReviewCommand patchReviewCommand;
  LocalDate publishDate;
  long reviewCount;
  String isbn;
  String thumbnailUrl;
  BigDecimal ratingInBook;

  String email;
  String nickname;
  String password;

  ReviewLike reviewLike;

  @BeforeEach
  void setUp() {

    title = "title";
    author = "author";
    description = "description";
    publisher = "publisher";
    publishDate = LocalDate.now();
    reviewCount = 0L;
    ratingInBook = BigDecimal.valueOf(1.1);
    isbn = "isbn";
    thumbnailUrl = "thumbnailUrl";

    book = Book.builder()
        .id(1L)
        .title(title)
        .author(author)
        .description(description)
        .publisher(publisher)
        .publishedDate(publishDate)
        .reviewCount(reviewCount)
        .rating(ratingInBook)
        .deleted(false)
        .isbn(isbn)
        .thumbnailUrl(thumbnailUrl)
        .build();

    email = "email";
    nickname = "nickname";
    password = "password";
    member = Member.builder()
        .id(1L)
        .email(email)
        .nickname(nickname)
        .password(password)
        .deleted(false)
        .build();

    notAllowedMember = Member.builder()
        .id(999L)
        .email(email)
        .nickname(nickname)
        .password(password)
        .deleted(false)
        .build();

    deleted = false;
    rating = 3;
    content = "content";

    review = Review.builder()
        .id(1L)
        .member(member)
        .book(book)
        .deleted(deleted)
        .likeCount(0L)
        .commentCount(0L)
        .rating(rating)
        .content(content)
        .createdAt(Instant.now())
        .updatedAt(Instant.now())
        .build();

    reviewLike = new ReviewLike(review, member);

    newContent = "newContent";
    newRating = (short) 5;
    patchReviewCommand = PatchReviewCommand.builder()
        .memberId(1L)
        .reviewId(1L)
        .newContent(newContent)
        .newRating(newRating)
        .build();
  }

  @Test
  @DisplayName("리뷰 수정 테스트 - 올바른 입력값이 주어졌을 때")
  void patchReviewWithRightInput() {
    // Given
    given(memberRepository.findByIdAndDeletedIsFalse(any())).willReturn(Optional.of(member));
    given(reviewRepository.findByIdAndDeletedIsFalse(any())).willReturn(Optional.of(review));
    given(reviewLikeRepository.findByMemberIdAndReviewId(any(), any()))
        .willReturn(Optional.of(reviewLike));

    // When
    PatchReviewResult result = reviewService.patchReview(patchReviewCommand);

    // Then
    assertThat(result).isNotNull();
    assertThat(result.getId()).isNotNull();
    assertThat(result.getBookId()).isEqualTo(book.getId());
    assertThat(result.getBookTitle()).isEqualTo(title);
    assertThat(result.getBookThumbnailUrl()).isEqualTo(thumbnailUrl);
    assertThat(result.getUserId()).isEqualTo(member.getId());
    assertThat(result.getUserNickname()).isEqualTo(nickname);
    assertThat(result.getContent()).isEqualTo(newContent);
    assertThat(result.getRating()).isEqualTo(newRating);
    assertThat(result.getLikeCount()).isEqualTo(review.getLikeCount());
    assertThat(result.getCommentCount()).isEqualTo(review.getCommentCount());
    assertThat(result.isLikedByMe()).isTrue();
    assertThat(result.getCreatedAt())
        .isEqualTo(review.getCreatedAt());

    assertThat(result.getUpdatedAt())
        .isEqualTo(review.getUpdatedAt());
  }

  @Test
  @DisplayName("리뷰 수정 테스트 - 권한이 없을 때")
  void patchReviewWithNotFoundEntities() {

    // Given
    given(memberRepository.findByIdAndDeletedIsFalse(any())).willReturn(
        Optional.of(notAllowedMember));
    given(reviewRepository.findByIdAndDeletedIsFalse(any())).willReturn(Optional.of(review));

    PatchReviewCommand NotAllowedCommand = PatchReviewCommand.builder()
        .memberId(notAllowedMember.getId()) // 권한 없는 사용자
        .reviewId(1L)
        .newContent(newContent)
        .newRating(newRating)
        .build();

    // When
    assertThatThrownBy(() -> reviewService.patchReview(NotAllowedCommand))
        .isInstanceOf(AuthorizationException.class)
        .hasMessageContaining("허용되지 않은");

  }


}
