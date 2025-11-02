package com.codeit.deokhugam.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.codeit.deokhugam.domain.entity.Book;
import com.codeit.deokhugam.domain.entity.Member;
import com.codeit.deokhugam.domain.entity.Review;
import jakarta.persistence.EntityManager;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;

@DataJpaTest
public class ReviewRepositoryTest extends DataBaseConnectionSupport {

  @Autowired
  private ReviewRepository reviewRepository;
  @Autowired
  private MemberRepository memberRepository;
  @Autowired
  private BookRepository bookRepository;

  @Autowired
  EntityManager em;

  Book book;
  Member member;

  Review review;
  boolean deleted;
  Short rating;
  String content;

  @BeforeEach
  void setUp() {
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
  }

  @Test
  @DisplayName("리뷰 저장 테스트 ")
  void insertReview() {
    reviewRepository.save(review);
    em.flush();
    em.clear();

    Optional<Review> storedReview = reviewRepository.findById(review.getId());
    assertThat(storedReview.isPresent()).isTrue();

  }

  @Test
  @DisplayName("리뷰 저장 테스트 중복된 리뷰 등록 요청이 있을 때 - 유저는 한 책에 대해 하나의 리뷰만 가능")
  void insertReviewWithDuplicate() {
    reviewRepository.save(review);
    em.flush();
    em.clear();

    Review r2 = Review.builder()
        .member(member)
        .book(book)
        .deleted(deleted)
        .likeCount(0L)
        .commentCount(0L)
        .rating(rating)
        .content(content)
        .build();

    // when & then
    assertThrows(DataIntegrityViolationException.class, () -> {
      reviewRepository.saveAndFlush(r2);
    });

  }


}
