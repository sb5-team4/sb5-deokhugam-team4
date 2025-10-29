package com.codeit.deokhugam.domain.entity;

import com.codeit.deokhugam.domain.entity.base.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Table(
    name = "review_like",
    uniqueConstraints = {
        @UniqueConstraint(name = "uq_review_like", columnNames = {"review_id", "member_id"})
    }
)
public class ReviewLike extends BaseEntity {

  @OneToOne
  @JoinColumn(name = "review_id", nullable = false)
  private Review review;
  @OneToOne
  @JoinColumn(name = "member_id", nullable = false)
  private Member member;
}
