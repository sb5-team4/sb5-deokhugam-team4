package com.codeit.deokhugam.domain.entity;

import com.codeit.deokhugam.domain.entity.base.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Builder
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PowerMember extends BaseEntity {


  @Column(name = "period", nullable = false)
  private String period; // enum
  @Column(name = "rank", nullable = false)
  private Long rank;
  @Column(name = "score", nullable = false)
  private BigDecimal score;
  @Column(name = "review_score_sum", nullable = false)
  private BigDecimal reviewScoreSum;
  @Column(name = "like_count", nullable = false)
  private Long likeCount;
  @Column(name = "comment_count", nullable = false)
  private Long commentCount;

  @ManyToOne
  @JoinColumn(name = "member_id", nullable = false)
  private Member member;
}
