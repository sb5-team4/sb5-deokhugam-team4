package com.codeit.deokhugam.domain.entity;

import com.codeit.deokhugam.domain.entity.base.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(
    name = "review_like",
    uniqueConstraints = {
        @UniqueConstraint(name = "uq_review_like", columnNames = {"review_id", "member_id"})
    }
)
public class ReviewLike extends BaseEntity {

  @OneToOne(orphanRemoval = true)
  @JoinColumn(name = "review_id", nullable = false)
  private Review review;
  @OneToOne(orphanRemoval = true)
  @JoinColumn(name = "member_id", nullable = false)
  private Member member;
}
