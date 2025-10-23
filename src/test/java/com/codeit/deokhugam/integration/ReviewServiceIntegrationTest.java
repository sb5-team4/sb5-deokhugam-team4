package com.codeit.deokhugam.integration;

import static org.assertj.core.api.Assertions.assertThat;

import com.codeit.deokhugam.domain.entity.Book;
import com.codeit.deokhugam.domain.entity.Member;
import com.codeit.deokhugam.domain.entity.Review;
import com.codeit.deokhugam.dto.command.CreateReviewCommand;
import com.codeit.deokhugam.dto.command.SoftDeleteReviewCommand;
import com.codeit.deokhugam.repository.BookRepository;
import com.codeit.deokhugam.repository.DataBaseConnectionSupport;
import com.codeit.deokhugam.repository.MemberRepository;
import com.codeit.deokhugam.repository.ReviewRepository;
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

  Book book;
  Member member;

  Review review;
  boolean deleted;
  Short rating;
  String content;

  @BeforeEach
  void setup() {
    book = Book.builder()
        .title("title")
        .author("author")
        .description("description")
        .publisher("publisher")
        .publishedDate(LocalDate.now())
        .reviewCount(0)
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
  }

  @Test
  @DisplayName("리뷰 생성시 Book review_count 필드 증가 확인")
  void BookReviewCountUpTestWhenReviewCreate() {
    int beforeCount = book.getReviewCount();

    CreateReviewCommand command = CreateReviewCommand.builder()
        .bookId(book.getId())
        .userId(member.getId())
        .content(content)
        .rating(rating)
        .build();
    reviewService.createReview(command);

    em.flush();
    em.clear();

    int afterCount = bookRepository.findById(book.getId()).get().getReviewCount();

    assertThat(afterCount).isEqualTo(beforeCount + 1);
  }

  @Test
  @DisplayName("리뷰 삭제시 Book review_count 필드 감소 확인")
  void BookReviewCountDownTestWhenReviewCreate() {
    // Given
    book.setReviewCount(book.getReviewCount() + 1);
    bookRepository.save(book);
    reviewRepository.save(review);
    em.flush();
    em.clear();

    int beforeCount = book.getReviewCount();

    SoftDeleteReviewCommand command = SoftDeleteReviewCommand.builder()
        .memberId(member.getId())
        .reviewId(review.getId())
        .build();
    reviewService.softDelete(command);
    em.flush();
    em.clear();

    int afterCount = bookRepository.findById(book.getId()).get().getReviewCount();

    assertThat(afterCount).isEqualTo(beforeCount - 1);
  }

}
