package com.codeit.deokhugam.domain.entity;

import com.codeit.deokhugam.domain.entity.base.BaseEntity;
import com.codeit.deokhugam.domain.enums.Period;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@SuperBuilder(toBuilder = true)
public class PopularReview extends BaseEntity {

  @Column(name = "rank", nullable = false)
  private Long rank;
  @Column(name = "score", nullable = false)
  private BigDecimal score;
  @Column(name = "period", nullable = false)
  private String period; // enum 필드
  @Column(name = "ordered", nullable = false)
  private boolean ordered; // enum 필드

  @ManyToOne
  @JoinColumn(name = "review_id", nullable = false)
  private Review review;

  /**
   * score 만 방영 순위 계산 후처리
   *
   * @param score
   * @param period
   * @param review
   * @return
   */
  public static PopularReview fromByScoreNonOrdered(
      BigDecimal score,
      Period period,
      Review review
  ) {

    return PopularReview.builder()
        .score(score)
        .period(period.name())
        .ordered(false)
        .review(review)
        .build();

  }

}
