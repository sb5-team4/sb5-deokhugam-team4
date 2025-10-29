package com.codeit.deokhugam.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.codeit.deokhugam.config.QuerydslConfig;
import com.codeit.deokhugam.domain.entity.Book;
import com.codeit.deokhugam.domain.entity.Comment;
import com.codeit.deokhugam.domain.entity.Member;
import com.codeit.deokhugam.domain.entity.Review;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.TimeZone;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;
import org.springframework.data.auditing.DateTimeProvider;

// JPA 테스트 관련 설정 활성화 (@Transactional 포함되어 있음)
@DataJpaTest
@Import({QuerydslConfig.class, CommentRepositoryTest.TestAuditConfiguration.class}) // Querydsl 및 Auditing Mock 설정 Import
public class CommentRepositoryTest extends DataBaseConnectionSupport { // Testcontainers 설정 상속

  // 테스트 시작 전 JVM 시간대 UTC로 설정
  @BeforeAll
  static void setUtcTimezone() {
    TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
  }

  // --- Mock DateTimeProvider 설정을 위한 내부 클래스 ---
  @TestConfiguration
  static class TestAuditConfiguration {
    @Bean
    @Primary // 기본 DateTimeProvider 대신 이 Mock Bean을 사용
    public DateTimeProvider testDateTimeProvider() {
      return mock(DateTimeProvider.class); // Mockito로 가짜 객체 생성
    }
  }
  // --- Mock 설정 끝 ---

  @Autowired
  private CommentRepository commentRepository;

  @Autowired
  private MemberRepository memberRepository;

  @Autowired
  private ReviewRepository reviewRepository;

  @Autowired
  private BookRepository bookRepository;

  @Autowired
  private TestEntityManager testEntityManager; // 테스트 데이터를 영속화하고 ID를 얻기 위해 사용

  @Autowired
  private DateTimeProvider dateTimeProvider; // Mock DateTimeProvider 주입

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
        .reviewCount(0)
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
    testEntityManager.flush();
    testEntityManager.clear();
  }

  // --- Helper Method (Auditing Mocking 방식) ---
  private Comment createAndSaveComment(String content, Review review, Member member, Instant expectedCreatedAt) {
    Comment comment = Comment.builder()
        .content(content)
        .review(review)
        .member(member)
        .build();

    // 저장하기 직전에 Mock DateTimeProvider 설정
    when(dateTimeProvider.getNow()).thenReturn(Optional.of(expectedCreatedAt));

    Comment saved = commentRepository.save(comment); // @CreatedDate가 Mock 시간 사용
    Long savedId = saved.getId();
    testEntityManager.flush(); // DB 반영 (원래 변수명 사용)
    testEntityManager.clear(); // 영속성 컨텍스트 초기화 (원래 변수명 사용)

    // DB에서 다시 조회하여 createdAt이 확실히 반영된 객체를 반환
    return commentRepository.findById(savedId)
        .orElseThrow(() -> new IllegalStateException("Failed to find saved comment with ID: " + savedId));
  }
  // --- Helper Method 끝 ---

  // 1. POST 댓글 생성 Test---------------------------------------------------------------------------
  @Test
  @DisplayName("댓글 저장 & comment의 id로 조회")
  void savedCommentAndFindByIdSuccess() {
    // given: 현재 시간을 Mock DateTimeProvider에 설정 (저장 시 사용될 시간)
    Instant now = Instant.now();
    when(dateTimeProvider.getNow()).thenReturn(Optional.of(now));

    Comment testComment = Comment.builder()
        .review(testReview)
        .member(testMember)
        .content("저장 테스트 댓글")
        .build();

    // when: 저장 (이때 @CreatedDate가 Mock 시간을 사용)
    Comment savedComment = commentRepository.save(testComment);
    Long savedCommentId = savedComment.getId();

    testEntityManager.flush(); // 원래 변수명 사용
    testEntityManager.clear(); // 원래 변수명 사용

    // then
    Optional<Comment> foundComment = commentRepository.findById(savedCommentId);
    assertThat(foundComment).isPresent();
    assertThat(foundComment.get().getMember().getId()).isEqualTo(testMember.getId());
    assertThat(foundComment.get().getReview().getId()).isEqualTo(testReview.getId());
    assertThat(foundComment.get().getContent()).isEqualTo("저장 테스트 댓글");
    // [수정] 시간 비교 제거
  }

  @Test
  @DisplayName("논리 삭제(@SQLDelete) 및 조회(@Where) 검증")
  void logicalDelete_And_FindBy_WhereClause_Success() {
    // given: 삭제할 댓글 미리 저장
    when(dateTimeProvider.getNow()).thenReturn(Optional.of(Instant.now())); // createdAt 설정
    Comment commentToDelete = Comment.builder()
        .content("삭제될 댓글")
        .member(testMember)
        .review(testReview)
        .build();
    Comment savedComment = commentRepository.save(commentToDelete);
    Long idToDelete = savedComment.getId();
    testEntityManager.flush();
    testEntityManager.clear();

    // when: 댓글 삭제
    commentRepository.deleteById(idToDelete);
    testEntityManager.flush();
    testEntityManager.clear();

    // then: 조회가 안 되어야 함
    Optional<Comment> foundOptional = commentRepository.findById(idToDelete);
    assertThat(foundOptional).isNotPresent();
  }


  // 2. GET 댓글 목록 조회 Test (cursorId 파라미터 추가) --------------------------------------------------

  @Test
  @DisplayName("댓글 목록 조회 - 첫 페이지(내림차순)")
  void findByCommentReviewIdWithCursorFirstPageDesc() {

    // given: 헬퍼 메서드로 정확한 createdAt 값으로 데이터 생성
    Instant now = Instant.now();
    Comment testComment1 = createAndSaveComment("댓글 1", testReview, testMember, now.minus(3, ChronoUnit.HOURS)); // 가장 오래됨
    Comment testComment2 = createAndSaveComment("댓글 2", testReview, testMember, now.minus(2, ChronoUnit.HOURS));
    Comment testComment3 = createAndSaveComment("댓글 3", testReview, testMember, now.minus(1, ChronoUnit.HOURS)); // 가장 최신

    // when: 첫 페이지 조회 (after=null, cursorId=null, limit=2, DESC)
    int limitComment = 2;
    List<Comment> commentList = commentRepository.findByCommentReviewIdWithCursor(testReview.getId(), "DESC", null, null, limitComment);

    // then: limit+1개인 3개가 최신순(c3, c2, c1)으로 조회되어야 함
    assertThat(commentList).hasSize(limitComment + 1);
    assertThat(commentList.get(0).getId()).isEqualTo(testComment3.getId());
    assertThat(commentList.get(1).getId()).isEqualTo(testComment2.getId());
    assertThat(commentList.get(2).getId()).isEqualTo(testComment1.getId());

  }

  @Test
  @DisplayName("댓글 목록 조회 - 다음 페이지(내림차순)")
  void findByCommentReviewIdWithCursorNextPageDesc() {
    Instant now = Instant.now();
    Comment testComment1 = createAndSaveComment("댓글 1", testReview, testMember, now.minus(3, ChronoUnit.HOURS)); // 가장 오래됨
    Comment testComment2 = createAndSaveComment("댓글 2", testReview, testMember, now.minus(2, ChronoUnit.HOURS));
    Comment testComment3 = createAndSaveComment("댓글 3", testReview, testMember, now.minus(1, ChronoUnit.HOURS)); // 가장 최신

    // when: 두 번째 페이지 조회 (after=c2의 createdAt, cursorId=c2의 ID, limit=2, DESC)
    int limitComment = 2;
    Instant after = testComment2.getCreatedAt(); // 보조 커서 (시간)
    Long cursorId = testComment2.getId();     // 메인 커서 (ID)
    List<Comment> commentList = commentRepository.findByCommentReviewIdWithCursor(testReview.getId(), "DESC", after, cursorId, limitComment);

    // then: 나머지 댓글 1개(c1)만 조회되어야 함
    assertThat(commentList).hasSize(1);
    assertThat(commentList.get(0).getId()).isEqualTo(testComment1.getId());
  }

  @Test
  @DisplayName("댓글 목록 조회 - 다음 페이지 없음 (내림차순)")
  void findByCommentReviewIdWithCursorNoNextPageDesc() {
    Instant now = Instant.now();
    Comment testComment1 = createAndSaveComment("댓글 1", testReview, testMember, now.minus(1, ChronoUnit.HOURS));

    int limitComment = 2;
    List<Comment> commentList = commentRepository.findByCommentReviewIdWithCursor(testReview.getId(), "DESC", null, null, limitComment);

    assertThat(commentList).hasSize(1);
    assertThat(commentList.get(0).getId()).isEqualTo(testComment1.getId());
  }

  @Test
  @DisplayName("댓글 목록 조회 - 첫 페이지(오름차순)")
  void findByCommentReviewIdWithCursorFirstPageAsc(){
    Instant now = Instant.now();
    Comment testComment1 = createAndSaveComment("댓글 1", testReview, testMember, now.minus(3, ChronoUnit.HOURS)); // 가장 오래됨
    Comment testComment2 = createAndSaveComment("댓글 2", testReview, testMember, now.minus(2, ChronoUnit.HOURS));
    Comment testComment3 = createAndSaveComment("댓글 3", testReview, testMember, now.minus(1, ChronoUnit.HOURS)); // 가장 최신

    int limitComment = 2;
    List<Comment> commentList = commentRepository.findByCommentReviewIdWithCursor(testReview.getId(), "ASC", null, null, limitComment);

    assertThat(commentList).hasSize(limitComment + 1);
    assertThat(commentList.get(0).getId()).isEqualTo(testComment1.getId());
    assertThat(commentList.get(1).getId()).isEqualTo(testComment2.getId());
    assertThat(commentList.get(2).getId()).isEqualTo(testComment3.getId());
  }

  @Test
  @DisplayName("댓글 목록 조회 - 다음 페이지(오름차순)")
  void findByCommentReviewIdWithCursorNextPageAsc(){
    Instant now = Instant.now();
    Comment testComment1 = createAndSaveComment("댓글 1", testReview, testMember, now.minus(3, ChronoUnit.HOURS)); // 가장 오래됨
    Comment testComment2 = createAndSaveComment("댓글 2", testReview, testMember, now.minus(2, ChronoUnit.HOURS));
    Comment testComment3 = createAndSaveComment("댓글 3", testReview, testMember, now.minus(1, ChronoUnit.HOURS)); // 가장 최신

    int limitComment = 2;
    Instant after = testComment2 .getCreatedAt(); // 보조 커서 (시간)
    Long cursorId = testComment2.getId();      // 메인 커서 (ID)
    // [수정!] cursorId 파라미터 전달
    List<Comment> commentList = commentRepository.findByCommentReviewIdWithCursor(testReview.getId(), "ASC", after, cursorId, limitComment);

    // then: 나머지 댓글 1개(c3)만 조회되어야 함
    assertThat(commentList).hasSize(1);
    assertThat(commentList.get(0).getId()).isEqualTo(testComment3.getId());
  }
}
