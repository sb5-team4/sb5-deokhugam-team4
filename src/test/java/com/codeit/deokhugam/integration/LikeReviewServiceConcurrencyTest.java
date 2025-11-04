//package com.codeit.deokhugam.integration;
//
//import static org.junit.jupiter.api.Assertions.assertEquals;
//
//import com.codeit.deokhugam.domain.entity.Book;
//import com.codeit.deokhugam.domain.entity.Member;
//import com.codeit.deokhugam.domain.entity.Review;
//import com.codeit.deokhugam.fixture.BookFixture;
//import com.codeit.deokhugam.fixture.MemberFixture;
//import com.codeit.deokhugam.fixture.ReviewFixture;
//import com.codeit.deokhugam.repository.BookRepository;
//import com.codeit.deokhugam.repository.DataBaseConnectionSupport;
//import com.codeit.deokhugam.repository.MemberRepository;
//import com.codeit.deokhugam.repository.ReviewLikeRepository;
//import com.codeit.deokhugam.repository.ReviewRepository;
//import com.codeit.deokhugam.service.LikeReviewCommand;
//import com.codeit.deokhugam.service.LikeReviewService;
//import jakarta.persistence.EntityManager;
//import java.time.Duration;
//import java.time.Instant;
//import java.util.List;
//import java.util.concurrent.CountDownLatch;
//import java.util.concurrent.ExecutorService;
//import java.util.concurrent.Executors;
//import java.util.concurrent.atomic.AtomicInteger;
//import java.util.stream.IntStream;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//
//@SpringBootTest
//public class LikeReviewServiceConcurrencyTest extends DataBaseConnectionSupport {
//
//  @Autowired
//  private LikeReviewService likeReviewService;
//
//  @Autowired
//  private MemberRepository memberRepository;
//
//  @Autowired
//  private ReviewRepository reviewRepository;
//
//  @Autowired
//  private ReviewLikeRepository reviewLikeRepository;
//
//  @Autowired
//  private EntityManager em;
//  @Autowired
//  private BookRepository bookRepository;
//
//  @Test
//  @DisplayName("하나의 리뷰에 10명의 사용자가 동시에 좋아요를 눌러도 count가 정확히 증가한다.")
//  public void testConcurrentLikeReview() throws Exception {
//    // Given
//    Member author = MemberFixture.createDefaultMember();
//    memberRepository.save(author);
//    Book book = BookFixture.createBookWithAllFields();
//    bookRepository.save(book);
//
//    Review review = ReviewFixture.createReview(book, author);
//    reviewRepository.save(review);
//
//    // 10명의 사용자 생성
//    List<Member> members = IntStream.rangeClosed(1, 10)
//        .mapToObj(i -> memberRepository.save(MemberFixture.createDefaultMember()))
//        .toList();
//
//    AtomicInteger successCount = new AtomicInteger(0);
//    ExecutorService executorService = Executors.newFixedThreadPool(10);
//
//    CountDownLatch startLatch = new CountDownLatch(1);
//    CountDownLatch endLatch = new CountDownLatch(10);
//
//    Instant start = Instant.now();
//    // When
//    for (Member member : members) {
//      executorService.submit(() -> {
//        try {
//          startLatch.await();
//          LikeReviewCommand command = LikeReviewCommand.builder()
//              .memberId(member.getId())
//              .reviewId(review.getId())
//              .build();
//
//          likeReviewService.likeReviewV2(command);
//          successCount.incrementAndGet();
//        } catch (Exception e) {
//          // 실패는 insertIgnoreConflict에 의해 발생할 수 있음 (중복방지)
//          System.out.println("실패...");
//          System.out.println(e.getMessage());
//        } finally {
//          endLatch.countDown();
//        }
//      });
//    }
//
//    startLatch.countDown(); // 모든 스레드 동시에 시작
//    endLatch.await();       // 모든 스레드 종료 대기
//    executorService.shutdown();
//
//    em.clear();
//    Review result = reviewRepository.findById(review.getId()).orElseThrow();
//    long actualCount = reviewLikeRepository.countByReviewId(result.getId());
//    System.out.println("✅ 성공적으로 좋아요 처리된 수: " + successCount.get());
//    System.out.println("❤️ ReviewLike 테이블 최종 좋아요 수: " + actualCount);
//    System.out.println("📝 Review 테이블 최종 좋아요 수: " + result.getLikeCount());
//    System.out.println("걸린 시간 : " + Duration.between(start, Instant.now()).toMillis() + "ms");
//
//    // Then
//    assertEquals(10, successCount.get());
//    assertEquals(10, result.getLikeCount());
//    assertEquals(10, reviewLikeRepository.countByReviewId(result.getId()));
//
//  }
//
//}
