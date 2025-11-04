package com.codeit.deokhugam.domain.entity;

import com.codeit.deokhugam.domain.entity.base.BaseEntity;
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

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class PopularBook extends BaseEntity {

  @Column(name = "period", nullable = false, length = 20)
  private String period;
  @Column(name = "rank", nullable = false)
  private Short rank;

  // score 계산식 : 리뷰수 * 0.4 + 평점 * 0.6
  @Column(name = "score", nullable = false, precision = 10, scale = 2)
  private BigDecimal score;

//  @Column(name = "review_count", nullable = false)
//  private Long reviewCount;
//  @Column(name = "rating", nullable = false, precision = 3, scale = 2)
//  private BigDecimal rating;

  // 하나의 Book이 기간별(DAILY, WEEKLY, MONTHLY, ALL_TIME) PopularBook을 가져야 하므로 ManyToOne
  @ManyToOne
  @JoinColumn(name = "book_id", nullable = false)
  private Book book;

}
