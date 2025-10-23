package com.codeit.deokhugam.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

import com.codeit.deokhugam.common.exception.AuthorizationException;
import com.codeit.deokhugam.common.exception.ResourceNotFoundException;
import com.codeit.deokhugam.domain.entity.Book;
import com.codeit.deokhugam.domain.entity.Member;
import com.codeit.deokhugam.domain.entity.Review;
import com.codeit.deokhugam.dto.command.CreateReviewCommand;
import com.codeit.deokhugam.dto.command.SoftDeleteReviewCommand;
import com.codeit.deokhugam.dto.result.CreateReviewResult;
import com.codeit.deokhugam.repository.BookRepository;
import com.codeit.deokhugam.repository.MemberRepository;
import com.codeit.deokhugam.repository.ReviewLikeRepository;
import com.codeit.deokhugam.repository.ReviewRepository;
import com.codeit.deokhugam.service.impl.ReviewServiceImpl;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@ExtendWith(MockitoExtension.class)
@EnableJpaAuditing
public class ReviewServiceTest {

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

  String title;
  String author;
  String description;
  String publisher;
  Review review;
  boolean deleted;
  Short rating;
  String content;
  CreateReviewCommand createReviewCommand;
  LocalDate publishDate;
  int reviewCount;
  String isbn;
  String thumbnailUrl;
  BigDecimal ratingInBook;

  String email;
  String nickname;
  String password;

  @BeforeEach
  void setUp() {

    title = "title";
    author = "author";
    description = "description";
    publisher = "publisher";
    publishDate = LocalDate.now();
    reviewCount = 0;
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

    createReviewCommand = CreateReviewCommand.builder()
        .bookId(book.getId())
        .userId(member.getId())
        .content(content)
        .rating(rating)
        .build();
  }

  @Test
  @DisplayName("리뷰 생성 테스트 -  올바른 입력값이 주어졌을 때")
  void createReviewWithRightInput() {
    // GIVEN
    given(bookRepository.findById(any())).willReturn(Optional.of(book));
    given(memberRepository.findById(any())).willReturn(Optional.of(member));

    given(reviewLikeRepository.findByMemberIdAndReviewId(any(), any()))
        .willReturn(Optional.empty());

    given(reviewRepository.save(any())).willReturn(review);

    // WHEN
    CreateReviewResult result = reviewService.createReview(createReviewCommand);

    // THEN
    assertThat(result).isNotNull();
    assertThat(result.getId()).isNotNull();
    assertThat(result.getBookId()).isEqualTo(book.getId());
    assertThat(result.getBookTitle()).isEqualTo(title);
    assertThat(result.getBookThumbnailUrl()).isEqualTo(thumbnailUrl);
    assertThat(result.getUserId()).isEqualTo(member.getId());
    assertThat(result.getUserNickname()).isEqualTo(nickname);
    assertThat(result.getContent()).isEqualTo(content);
    assertThat(result.getRating()).isEqualTo(rating);
    assertThat(result.getLikeCount()).isEqualTo(review.getLikeCount());
    assertThat(result.getCommentCount()).isEqualTo(review.getCommentCount());
    assertThat(result.isLikedByMe()).isFalse();
    assertThat(result.getCreatedAt())
        .isEqualTo(OffsetDateTime.ofInstant(review.getCreatedAt(), ZoneOffset.ofHours(9)));

    assertThat(result.getUpdatedAt())
        .isEqualTo(OffsetDateTime.ofInstant(review.getUpdatedAt(), ZoneOffset.ofHours(9)));

  }

  @Test
  @DisplayName("리뷰 생성 테스트 -  존재하지않은 Book 입력")
  void createReviewWithNotFoundBookId() {
    given(bookRepository.findById(any())).willReturn(Optional.empty());

    assertThatThrownBy(() -> reviewService.createReview(createReviewCommand))
        .isInstanceOf(ResourceNotFoundException.class)
        .hasMessageContaining("bookId with");
  }

  @Test
  @DisplayName("리뷰 생성 테스트 -  존재하지않은 Member 입력")
  void createReviewWithNotFoundMemberId() {
    given(bookRepository.findById(any())).willReturn(Optional.of(book));
    given(memberRepository.findById(any())).willReturn(Optional.empty());

    assertThatThrownBy(() -> reviewService.createReview(createReviewCommand))
        .isInstanceOf(ResourceNotFoundException.class)
        .hasMessageContaining("memberId with");
  }

  @Test
  @DisplayName("리뷰 삭제 테스트 -  정상 동작")
  void softDeleteReview() {
    // Given
    given(reviewRepository.findById(any())).willReturn(Optional.of(review));

    // When
    boolean result = reviewService.softDelete(SoftDeleteReviewCommand.builder()
        .reviewId(review.getId())
        .memberId(member.getId())
        .build());

    // Then
    assertThat(result).isTrue();


  }

  @Test
  @DisplayName("리뷰 삭제 테스트 - 권한이 없을 때")
  void softDeleteReviewWithNotAllowed() {
    // Given
    Member anotherMember = Member.builder()
        .id(2L)
        .email(email)
        .nickname(nickname)
        .password(password)
        .deleted(false)
        .build();
    Review NotAllowedReview = Review.builder()
        .id(1L)
        .member(anotherMember)
        .book(book)
        .deleted(deleted)
        .likeCount(0L)
        .commentCount(0L)
        .rating(rating)
        .content(content)
        .createdAt(Instant.now())
        .updatedAt(Instant.now())
        .build();

    given(reviewRepository.findById(any())).willReturn(Optional.of(NotAllowedReview));
    SoftDeleteReviewCommand command = SoftDeleteReviewCommand.builder()
        .reviewId(NotAllowedReview.getId())
        .memberId(member.getId())
        .build();

    // When // Then
    assertThatThrownBy(() -> reviewService.softDelete(command))
        .isInstanceOf(AuthorizationException.class)
        .hasMessageContaining("허용 되지 않은 연산입니다.");

  }

  @Test
  @DisplayName("리뷰 삭제 테스트 - 리뷰가 없을 때")
  void softDeleteReviewWithNotFoundReview() {
    // Given
    given(reviewRepository.findById(any())).willReturn(Optional.empty());
    SoftDeleteReviewCommand command = SoftDeleteReviewCommand.builder()
        .reviewId(review.getId())
        .memberId(member.getId())
        .build();

    // When Then
    assertThatThrownBy(() -> reviewService.softDelete(command))
        .isInstanceOf(ResourceNotFoundException.class)
        .hasMessageContaining("reviewId with " + review.getId() + " not found");
  }

}
