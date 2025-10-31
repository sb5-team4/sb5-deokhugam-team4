package com.codeit.deokhugam.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.codeit.deokhugam.domain.entity.Book;
import com.codeit.deokhugam.domain.entity.Comment;
import com.codeit.deokhugam.domain.entity.Member;
import com.codeit.deokhugam.domain.entity.Review;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

// JPA 테스트 관련 설정 활성화 (@Transactional 포함되어 있음)
@DataJpaTest
public class CommentRepositoryTest extends DataBaseConnectionSupport { //Testcontainers 설정 상속

  @Autowired
  private CommentRepository commentRepository;

  @Autowired
  private MemberRepository memberRepository;

  @Autowired
  private ReviewRepository reviewRepository;

  @Autowired
  private BookRepository bookRepository;

  // 7. 테스트 데이터를 영속화하고 ID를 얻기 위해 사용
  @Autowired
  private TestEntityManager em;

  private Member testMember;
  private Review testReview;
  private Book testBook;

  boolean deleted = false;
  Short rating = 3;

  // Member와 Review 미리 저장
  @BeforeEach
  public void setUp() {

    testMember = Member.builder()
        .email("test@example.com")
        .nickname("테스트 멤버")
        .password("password1234")
        .deleted(false)
        .build();

    testBook = Book.builder()
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

    testReview = Review.builder()
        .member(testMember)
        .book(testBook)
        .deleted(deleted)
        .likeCount(0L)
        .commentCount(0L)
        .rating(rating)
        .content("테스트 리뷰")
        .build();

    testMember = memberRepository.save(testMember);
    testBook = bookRepository.save(testBook);
    testReview = reviewRepository.save(testReview);

    // flush & clear: ID가 생성되고 영속성 컨텍스트를 비워서 다음 작업 준비
    em.flush();
    em.clear();
  }

  @Test
  @DisplayName("댓글 저장 & comment의 id로 조회")
  void savedCommentAndFindByIdSuccess() {
    Comment testComment = Comment.builder()
        .review(testReview)
        .member(testMember)
        .content("저장 테스트 댓글")
        .build();

    Comment savedComment = commentRepository.save(testComment);
    Long savedCommentId = savedComment.getId();

    em.flush();
    em.clear();

    Optional<Comment> foundComment = commentRepository.findById(savedCommentId);
    assertThat(foundComment).isPresent();
    assertThat(foundComment.get().getMember().getId()).isEqualTo(testMember.getId());
    assertThat(foundComment.get().getReview().getId()).isEqualTo(testReview.getId());
    assertThat(foundComment.get().getContent()).isEqualTo("저장 테스트 댓글");

  }
}
