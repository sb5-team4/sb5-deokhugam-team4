package com.codeit.deokhugam.domain.entity;

import com.codeit.deokhugam.domain.entity.base.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import java.math.BigDecimal;

@Entity
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

  @OneToOne(orphanRemoval = true)
  @JoinColumn(name = "member_id", nullable = false)
  private Member member;
}
