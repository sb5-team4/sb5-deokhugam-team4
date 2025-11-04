package com.codeit.deokhugam.integration;

import static org.assertj.core.api.Assertions.assertThat;

import com.codeit.deokhugam.domain.entity.Book;
import com.codeit.deokhugam.domain.entity.Comment;
import com.codeit.deokhugam.domain.entity.Member;
import com.codeit.deokhugam.domain.entity.Notification;
import com.codeit.deokhugam.domain.entity.PopularReview;
import com.codeit.deokhugam.domain.entity.Review;
import com.codeit.deokhugam.domain.entity.ReviewLike;
import com.codeit.deokhugam.dto.command.CreateReviewCommand;
import com.codeit.deokhugam.dto.command.HardDeleteReviewCommand;
import com.codeit.deokhugam.dto.command.PatchReviewCommand;
import com.codeit.deokhugam.dto.command.SoftDeleteReviewCommand;
import com.codeit.deokhugam.repository.CommentRepository;
import com.codeit.deokhugam.repository.DataBaseConnectionSupport;
import com.codeit.deokhugam.repository.MemberRepository;
import com.codeit.deokhugam.repository.NotificationRepository;
import com.codeit.deokhugam.repository.PopularReviewRepository;
import com.codeit.deokhugam.repository.ReviewLikeRepository;
import com.codeit.deokhugam.repository.ReviewRepository;
import com.codeit.deokhugam.repository.book.BookRepository;
import com.codeit.deokhugam.service.LikeReviewCommand;
import com.codeit.deokhugam.service.LikeReviewService;
import com.codeit.deokhugam.service.ReviewService;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import java.math.BigDecimal;
import java.time.LocalDate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@Transactional
public class ReviewServiceIntegrationTest extends DataBaseConnectionSupport {

  @Autowired
  private ReviewService reviewService;
  @Autowired
  private BookRepository bookRepository;
  @Autowired
  private MemberRepository memberRepository;
  @Autowired
  private EntityManager em;
  @Autowired
  private ReviewRepository reviewRepository;
  @Autowired
  ReviewLikeRepository reviewLikeRepository;
  @Autowired
  PopularReviewRepository popularReviewRepository;
  @Autowired
  NotificationRepository notificationRepository;
  @Autowired
  LikeReviewService likeReviewService;

  Book book;
  Member member;

  Review review;
  boolean deleted;
  Short rating;
  String content;

  ReviewLike reviewLike;

  Long rank;
  BigDecimal score;
  String period;
  PopularReview popularReview;

  Comment comment;
  boolean confirmed;
  Notification notification;
  @Autowired
  private CommentRepository commentRepository;

  @BeforeEach
  void setup() {
    book = Book.builder()
        .title("title")
        .author("author")
        .description("description")
        .publisher("publisher")
        .publishedDate(LocalDate.now())
        .reviewCount(0L)
        .rating(BigDecimal.valueOf(1.1))
        .deleted(false)
        .isbn("isbn")
        .thumbnailUrl("thumbnail_url")
        .build();
    bookRepository.save(book);

    member = Member.builder()
        .email("email")
        .nickname("nickname")
        .password("password")
        .deleted(false)
        .build();
    memberRepository.save(member);
    em.flush();
    em.clear();
    deleted = false;
    rating = 3;
    content = "content";

    review = Review.builder()
        .member(member)
        .book(book)
        .deleted(deleted)
        .likeCount(0L)
        .commentCount(0L)
        .rating(rating)
        .content(content)
        .build();

    rank = 1L;
    score = BigDecimal.valueOf(1.1);
    period = "period";
    confirmed = false;

    reviewLike = new ReviewLike(review, member);
    popularReview = PopularReview.builder()
        .rank(rank)
        .score(score)
        .period(period)
        .review(review)
        .build();

    comment = Comment.builder()
        .content(content)
        .deleted(deleted)
        .review(review)
        .member(member)
        .build();

    notification = Notification
        .builder()
        .content(content)
        .confirmed(confirmed)
        .deleted(deleted)
        .member(member)
        .review(review)
        .build();

  }

  @Test
  @DisplayName("리뷰 생성시 Book review_count 필드 증가 확인")
  void BookReviewCountUpTestWhenReviewCreate() {
    long beforeCount = book.getReviewCount();

    CreateReviewCommand command = CreateReviewCommand.builder()
        .bookId(book.getId())
        .userId(member.getId())
        .content(content)
        .rating(rating)
        .build();
    reviewService.createReview(command);

    em.flush();
    em.clear();

    long afterCount = bookRepository.findById(book.getId()).get().getReviewCount();

    assertThat(afterCount).isEqualTo(beforeCount + 1);
  }

  @Test
  @DisplayName("리뷰 삭제시 Book review_count 필드 감소 및 삭제 확인")
  void BookReviewCountDownTestWhenReviewCreate() {
    // Given
    book.setReviewCount(book.getReviewCount() + 1);
    bookRepository.save(book);
    reviewRepository.save(review);
    em.flush();
    em.clear();

    long beforeCount = book.getReviewCount();

    SoftDeleteReviewCommand command = SoftDeleteReviewCommand.builder()
        .memberId(member.getId())
        .reviewId(review.getId())
        .build();
    reviewService.softDelete(command);
    em.flush();
    em.clear();

    Review deletedReview = reviewRepository.findById(review.getId()).get();

    long afterCount = bookRepository.findById(book.getId()).get().getReviewCount();

    assertThat(deletedReview.isDeleted()).isTrue();
    assertThat(afterCount).isEqualTo(beforeCount - 1);
  }

  @Test
  @DisplayName("리뷰 물리 삭제시 count 가 내려가는 경우")
  void ReviewHardDeleteThenCountDown() {
    // Given
    book.setReviewCount(book.getReviewCount() + 1);
    bookRepository.save(book);
    reviewRepository.save(review);
    em.flush();
    em.clear();

    long beforeCount = book.getReviewCount();

    HardDeleteReviewCommand command = HardDeleteReviewCommand.builder()
        .memberId(member.getId())
        .reviewId(review.getId())
        .build();
    reviewService.hardDelete(command);
    em.flush();
    em.clear();

    long afterCount = bookRepository.findById(book.getId()).get().getReviewCount();

    assertThat(afterCount).isEqualTo(beforeCount - 1);

  }

  @Test
  @DisplayName("리뷰 물리 삭제시 count 가 안 내려가는 경우")
  void ReviewHardDeleteThenNoCount() {

    book.setReviewCount(book.getReviewCount() + 1);
    bookRepository.save(book);
    review.setDeleted(true);
    reviewRepository.save(review);
    em.flush();
    em.clear();

    long beforeCount = book.getReviewCount();

    HardDeleteReviewCommand command = HardDeleteReviewCommand.builder()
        .memberId(member.getId())
        .reviewId(review.getId())
        .build();
    reviewService.hardDelete(command);
    em.flush();
    em.clear();

    long afterCount = bookRepository.findById(book.getId()).get().getReviewCount();

    assertThat(afterCount).isEqualTo(beforeCount);
  }

  @Test
  @DisplayName("리뷰 물리 삭제 테스트 - 자식 엔티티가 삭제 되는지")
  void softDeleteReviewThenReviewLikeDelete() {
    // Given
    reviewRepository.save(review);
    reviewLikeRepository.save(reviewLike);
    popularReviewRepository.save(popularReview);
    commentRepository.save(comment);
    notificationRepository.save(notification);
    em.flush();
    em.clear();

    HardDeleteReviewCommand command = HardDeleteReviewCommand.builder()
        .reviewId(review.getId())
        .memberId(member.getId())
        .build();

    // When
    reviewService.hardDelete(command);
    em.flush();
    em.clear();

    // Then
    assertThat(reviewLikeRepository.existsById(reviewLike.getId())).isFalse();
    assertThat(commentRepository.existsById(comment.getId())).isFalse();
    assertThat(popularReviewRepository.existsById(popularReview.getId())).isFalse();
    assertThat(notificationRepository.existsById(notification.getId())).isFalse();
  }

  @Test
  @DisplayName("리뷰 좋아요 시 리뷰 테이블 좋아요 수 필드 count up")
  void LikeReviewThenReviewLikeCountUp() {
    review.setLikeCount(1L);
    reviewRepository.save(review);
    em.flush();
    em.clear();

    long beforeCount = review.getLikeCount();

    LikeReviewCommand command = LikeReviewCommand.builder()
        .memberId(member.getId())
        .reviewId(review.getId())
        .build();
    likeReviewService.likeReview(command);
    em.flush();
    em.clear();

    long afterCount = reviewRepository.findById(review.getId()).get().getLikeCount();

    assertThat(afterCount).isEqualTo(beforeCount + 1);
  }

  @Test
  @DisplayName("리뷰 좋아요 취소시 리뷰 테이블 좋아요 수 필드 count down")
  void UnlikeReviewThenReviewLikeCountDown() {
    // Given
    review.setLikeCount(1L);
    reviewRepository.save(review);
    em.flush();
    em.clear();

    LikeReviewCommand upCommand = LikeReviewCommand.builder()
        .memberId(member.getId())
        .reviewId(review.getId())
        .build();
    LikeReviewCommand downCommand = LikeReviewCommand.builder()
        .memberId(member.getId())
        .reviewId(review.getId())
        .build();

    likeReviewService.likeReview(upCommand);
    em.flush();
    em.clear();

    // When
    long beforeCount = reviewRepository.findById(review.getId()).get().getLikeCount();
    likeReviewService.likeReview(downCommand);
    em.flush();
    em.clear();
    long afterCount = reviewRepository.findById(review.getId()).get().getLikeCount();

    // Then
    assertThat(afterCount).isEqualTo(beforeCount - 1);
  }

  @Test
  @DisplayName("리뷰 수정 시 업데이트 여부 확인")
  void patchReviewThenReviewFieldUpdate() {
    reviewRepository.save(review);
    em.flush();
    em.clear();

    String newContent = "newContent";
    short newRating = 5;
    PatchReviewCommand command = PatchReviewCommand.builder()
        .memberId(member.getId())
        .reviewId(review.getId())
        .newContent(newContent)
        .newRating(newRating)
        .build();

    String beforeContent = review.getContent();
    Short beforeRating = review.getRating();

    reviewService.patchReview(command);
    em.flush();
    em.clear();

    Review afterReview = reviewRepository.findById(review.getId()).get();
    assertThat(afterReview.getContent()).isEqualTo(newContent);
    assertThat(afterReview.getContent()).isNotEqualTo(beforeContent);
    assertThat(afterReview.getRating()).isEqualTo(newRating);
    assertThat(afterReview.getRating()).isNotEqualTo(beforeRating);


  }

  @Test
  @DisplayName("리뷰 좋아요 시 리뷰 테이블 좋아요 수 필드 count up")
  void LikeReviewV2ThenReviewLikeCountUp() {
    review.setLikeCount(1L);
    reviewRepository.save(review);
    em.flush();
    em.clear();

    long beforeCount = review.getLikeCount();
    System.out.println("@@@@");
    System.out.println(member);
    System.out.println(member.getId());

    LikeReviewCommand command = LikeReviewCommand.builder()
        .memberId(member.getId())
        .reviewId(review.getId())
        .build();
    likeReviewService.likeReviewV2(command);
    em.flush();
    em.clear();

    long afterCount = reviewRepository.findById(review.getId()).get().getLikeCount();

    assertThat(afterCount).isEqualTo(beforeCount + 1);
  }

  @Test
  @DisplayName("리뷰 좋아요 취소시 리뷰 테이블 좋아요 수 필드 count down")
  void UnlikeReviewV2ThenReviewLikeCountDown() {
    // Given
    review.setLikeCount(1L);
    reviewRepository.save(review);
    em.flush();
    em.clear();

    LikeReviewCommand upCommand = LikeReviewCommand.builder()
        .memberId(member.getId())
        .reviewId(review.getId())
        .build();
    LikeReviewCommand downCommand = LikeReviewCommand.builder()
        .memberId(member.getId())
        .reviewId(review.getId())
        .build();

    likeReviewService.likeReviewV2(upCommand);
    em.flush();
    em.clear();

    // When
    long beforeCount = reviewRepository.findById(review.getId()).get().getLikeCount();
    likeReviewService.likeReviewV2(downCommand);
    em.flush();
    em.clear();
    long afterCount = reviewRepository.findById(review.getId()).get().getLikeCount();

    // Then
    assertThat(afterCount).isEqualTo(beforeCount - 1);
  }

}
