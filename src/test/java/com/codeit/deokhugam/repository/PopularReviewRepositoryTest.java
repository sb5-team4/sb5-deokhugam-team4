package com.codeit.deokhugam.repository;

import static com.codeit.deokhugam.domain.enums.Period.DAILY;
import static org.assertj.core.api.Assertions.assertThat;

import com.codeit.deokhugam.domain.entity.Book;
import com.codeit.deokhugam.domain.entity.Member;
import com.codeit.deokhugam.domain.entity.PopularReview;
import com.codeit.deokhugam.domain.entity.Review;
import com.codeit.deokhugam.dto.result.PaginatedResult;
import com.codeit.deokhugam.fixture.BookFixture;
import com.codeit.deokhugam.fixture.MemberFixture;
import com.codeit.deokhugam.fixture.PopularReviewFixture;
import com.codeit.deokhugam.fixture.ReviewFixture;
import jakarta.persistence.EntityManager;
import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Sort.Direction;

@DataJpaTest
public class PopularReviewRepositoryTest extends DataBaseConnectionSupport {

  @Autowired
  private PopularReviewRepository popularReviewRepository;
  @Autowired
  private ReviewRepository reviewRepository;
  @Autowired
  private MemberRepository memberRepository;
  @Autowired
  private BookRepository bookRepository;

  @Autowired
  EntityManager em;

  Book book1;

  Member member1;
  Member member2;
  Member member3;


  Review review1;
  Review review2;
  Review review3;

  BigDecimal score1 = BigDecimal.valueOf(1);
  BigDecimal score2 = BigDecimal.valueOf(1);
  BigDecimal score3 = BigDecimal.valueOf(1);

  PopularReview popularReview1;
  PopularReview popularReview2;
  PopularReview popularReview3;


  @BeforeEach
  void setup() {
    popularReviewRepository.deleteAll();
    em.flush();
    em.clear();

    book1 = BookFixture.createBookWithAllFields();
    bookRepository.save(book1);

    member1 = MemberFixture.createDefaultMember();
    member2 = MemberFixture.createDefaultMember();
    member3 = MemberFixture.createDefaultMember();

    memberRepository.saveAll(List.of(
        member1,
        member2,
        member3
    ));

    review1 = ReviewFixture.createReview(book1, member1);
    review2 = ReviewFixture.createReview(book1, member2);
    review3 = ReviewFixture.createReview(book1, member3);

    reviewRepository.saveAll(List.of(
        review1,
        review2,
        review3
    ));

    popularReview1 = PopularReviewFixture.createReview(review1, 1, DAILY)
        .toBuilder()
        .score(score1)
        .build();

    popularReview2 = PopularReviewFixture.createReview(review2, 2, DAILY)
        .toBuilder()
        .score(score2)
        .build();

    popularReview3 = PopularReviewFixture.createReview(review3, 3, DAILY)
        .toBuilder()
        .score(score3)
        .build();

    popularReviewRepository.saveAll(List.of(
        popularReview1,
        popularReview2,
        popularReview3
    ));

    em.flush();
    em.clear();

  }

  @Test
  @DisplayName("Cursor 없을 때 잘 조회 되는지 확인")
  public void getRanking() {
    // when
    PaginatedResult<PopularReview, Long> result = popularReviewRepository.searchWithCursor(
        DAILY,
        Direction.ASC,
        50,
        null,
        null
    );

    // content 검사
    assertThat(result.getContent()).hasSize(3);
    assertThat(result.getContent().get(0).getRank())
        .isEqualTo(popularReview1.getRank());
    assertThat(result.getContent().get(1).getRank())
        .isEqualTo(popularReview2.getRank());
    assertThat(result.getContent().get(2).getRank())
        .isEqualTo(popularReview3.getRank());

    // 페이징 데이터 검사
    assertThat(result.getNextCursor()).isEqualTo(popularReview3.getRank());
    assertThat(result.getNextAfter()).isEqualTo(popularReview3.getCreatedAt());
    assertThat(result.getSize()).isEqualTo(3);
    assertThat(result.getTotalElements()).isEqualTo(3);
    assertThat(result.getHasNext()).isFalse();

  }

  @Test
  @DisplayName("Cursor 있고 내림차순 잘 조회 되는지 확인")
  public void getRankingWithCursorAndDesc() {
    // when
    PaginatedResult<PopularReview, Long> result = popularReviewRepository.searchWithCursor(
        DAILY,
        Direction.DESC,
        1,
        popularReview3.getRank(),
        popularReview3.getCreatedAt()
    );

    // content 검사
    assertThat(result.getContent().get(0).getRank())
        .isEqualTo(popularReview2.getRank());

    // 페이징 데이터 검사
    assertThat(result.getNextCursor()).isEqualTo(popularReview2.getRank());
    assertThat(result.getNextAfter()).isEqualTo(popularReview2.getCreatedAt());
    assertThat(result.getSize()).isEqualTo(1);
    assertThat(result.getTotalElements()).isEqualTo(3);
    assertThat(result.getHasNext()).isTrue();

  }

}
