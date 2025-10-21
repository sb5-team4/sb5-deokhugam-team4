package com.codeit.deokhugam.domain.entity;

import com.codeit.deokhugam.domain.entity.base.BaseUpdatableEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
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
@SuperBuilder
public class PopularReview extends BaseUpdatableEntity {

  @Column(name = "rank", nullable = false)
  private Short rank;
  @Column(name = "score", nullable = false)
  private Short score;
  @Column(name = "period", nullable = false)
  private String period; // enum 필드

  @OneToOne(orphanRemoval = true)
  @JoinColumn(name = "review_id", nullable = false)
  private Review review;

}
