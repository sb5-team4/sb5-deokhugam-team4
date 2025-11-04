package com.codeit.deokhugam.batch;

import static org.assertj.core.api.Assertions.assertThat;

import com.codeit.deokhugam.domain.entity.Book;
import com.codeit.deokhugam.domain.entity.Comment;
import com.codeit.deokhugam.domain.entity.Member;
import com.codeit.deokhugam.domain.entity.Notification;
import com.codeit.deokhugam.domain.entity.Review;
import com.codeit.deokhugam.repository.CommentRepository;
import com.codeit.deokhugam.repository.MemberRepository;
import com.codeit.deokhugam.repository.NotificationRepository;
import com.codeit.deokhugam.repository.ReviewRepository;
import com.codeit.deokhugam.repository.book.BookRepository;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.batch.test.context.SpringBatchTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

@SpringBatchTest
@SpringBootTest
@ActiveProfiles("test")
@Transactional
class NotificationBatchTest {

  @Autowired
  private JobLauncherTestUtils jobLauncherTestUtils;

  @Autowired
  private Job deleteOldNotificationsJob;

  @Autowired
  private NotificationRepository notificationRepository;
  @Autowired
  private MemberRepository memberRepository;
  @Autowired
  private ReviewRepository reviewRepository;
  @Autowired
  private CommentRepository commentRepository;
  @Autowired
  private BookRepository bookRepository;

  private Member testMember;
  private Book testBook;
  private Review testReview;
  private Comment testComment;

  @BeforeEach
  void setUp() {
    // (배치 Job은 별도 트랜잭션이므로, @Transactional 롤백과 별도로 수동 초기화)
    notificationRepository.deleteAllInBatch();
    commentRepository.deleteAllInBatch();
    reviewRepository.deleteAllInBatch();
    bookRepository.deleteAllInBatch();
    memberRepository.deleteAllInBatch();

    // 1. 테스트용 기본 데이터 생성
    testMember = memberRepository.save(Member.builder()
        .email("batch@test.com")
        .nickname("batchUser")
        .password("password")
        .deleted(false)
        .build());

    testBook = bookRepository.save(Book.builder()
        .title("테스트 책")
        .author("테스트 저자")
        .description("설명")
        .publisher("출판사")
        .publishedDate(LocalDate.now())
        .reviewCount(0L)
        .rating(BigDecimal.valueOf(0.0))
        .deleted(false)
        .createdAt(Instant.now())
        .build());

    testReview = reviewRepository.save(Review.builder()
        .member(testMember)
        .book(testBook)
        .rating((short) 5)
        .content("테스트 리뷰")
        .deleted(false)
        .build());

    testComment = commentRepository.save(Comment.builder()
        .member(testMember)
        .review(testReview)
        .content("테스트 댓글")
        .deleted(false)
        .build());
  }

  @Test
  @DisplayName("알림 삭제 배치(deleteOldNotificationsJob) 실행 시, 1주일 경과 + 확인(confirmed)된 모든 알림(댓글, 랭킹 포함)만 삭제한다.")
  void deleteOldNotificationsJob_Test() throws Exception {
    // given
    Instant now = Instant.now();
    Instant eightDaysAgo = now.minus(8, ChronoUnit.DAYS); // 8일 전 (삭제 대상)
    Instant sixDaysAgo = now.minus(6, ChronoUnit.DAYS);   // 6일 전 (삭제 대상 아님)

    // --- (댓글 알림 3종) ---
    // 1. (삭제 대상 O) - 8일 전, 확인
    Notification noti_A = Notification.builder()
        .member(testMember).review(testReview).comment(testComment)
        .content("A (댓글)").confirmed(true).deleted(false)
        .updatedAt(eightDaysAgo) // (ItemReader의 'updatedAt < cutoffDate' 조건)
        .build();

    // 2. (삭제 대상 X) - 8일 전, 미확인
    Notification noti_B = Notification.builder()
        .member(testMember).review(testReview).comment(testComment)
        .content("B (댓글)").confirmed(false).deleted(false) // 'confirmed=false'라서 삭제되면 안 됨
        .updatedAt(eightDaysAgo)
        .build();

    // 3. (삭제 대상 X) - 6일 전, 확인
    Notification noti_C = Notification.builder()
        .member(testMember).review(testReview).comment(testComment)
        .content("C (댓글)").confirmed(true).deleted(false)
        .updatedAt(sixDaysAgo) // '1주일 미만'이라서 삭제되면 안 됨
        .build();

    // --- (랭킹 알림 2종 - comment_id가 NULL인 경우) ★★★★★
    // 4. (삭제 대상 O) - 8일 전, 확인, (랭킹 알림)
    Notification noti_D_Ranking = Notification.builder()
        .member(testMember).review(testReview).comment(null) // ★ comment_id = NULL
        .content("D (랭킹)").confirmed(true).deleted(false)
        .updatedAt(eightDaysAgo) // '1주일 경과' + '확인' (삭제 대상)
        .build();

    // 5. (삭제 대상 X) - 6일 전, 확인, (랭킹 알림)
    Notification noti_E_Ranking = Notification.builder()
        .member(testMember).review(testReview).comment(null) // ★ comment_id = NULL
        .content("E (랭킹)").confirmed(true).deleted(false)
        .updatedAt(sixDaysAgo) // '1주일 미만' (삭제 대상 아님)
        .build();

    // DB에 5개 저장
    notificationRepository.saveAll(List.of(noti_A, noti_B, noti_C, noti_D_Ranking, noti_E_Ranking));

    // JobParameters (time으로 매번 새 Job 실행)
    JobParameters jobParameters = new JobParametersBuilder()
        .addLong("time", System.currentTimeMillis())
        .toJobParameters();

    jobLauncherTestUtils.setJob(deleteOldNotificationsJob);

    // when
    // 배치 Job 실행!
    JobExecution jobExecution = jobLauncherTestUtils.launchJob(jobParameters);

    // then
    // 1. 배치 Job 실행 성공 검증
    assertThat(jobExecution.getStatus()).isEqualTo(BatchStatus.COMPLETED);

    // 2. (삭제 대상 O) 1번 알림(A)이 DB에서 삭제되었는지 검증
    Optional<Notification> deletedNoti_A = notificationRepository.findById(noti_A.getId());
    assertThat(deletedNoti_A.isPresent()).isFalse();

    // 3. (삭제 대상 X) 2번 알림(B)이 DB에 남아있는지 검증
    Optional<Notification> notDeletedNoti_B = notificationRepository.findById(noti_B.getId());
    assertThat(notDeletedNoti_B.isPresent()).isTrue();

    // 4. (삭제 대상 X) 3번 알림(C)이 DB에 남아있는지 검증
    Optional<Notification> notDeletedNoti_C = notificationRepository.findById(noti_C.getId());
    assertThat(notDeletedNoti_C.isPresent()).isTrue();

    // 5. (삭제 대상 O) 4번 랭킹 알림(D)이 DB에서 삭제되었는지 검증
    Optional<Notification> deletedNoti_D = notificationRepository.findById(noti_D_Ranking.getId());
    assertThat(deletedNoti_D.isPresent()).isFalse();

    // 6. (삭제 대상 X) 5번 랭킹 알림(E)이 DB에 남아있는지 검증
    Optional<Notification> notDeletedNoti_E = notificationRepository.findById(noti_E_Ranking.getId());
    assertThat(notDeletedNoti_E.isPresent()).isTrue();
  }
}