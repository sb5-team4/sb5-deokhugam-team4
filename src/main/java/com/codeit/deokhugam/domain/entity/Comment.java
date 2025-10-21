package com.codeit.deokhugam.domain.entity;

import com.codeit.deokhugam.domain.entity.base.BaseUpdatableEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;


@Entity
public class Comment extends BaseUpdatableEntity {

  @Column(name = "content", nullable = false)
  private String content;
  @Column(name = "deleted", nullable = false)
  private boolean deleted;

  @OneToOne(orphanRemoval = true)
  @JoinColumn(name = "review_id", nullable = false)
  private Review review;
  @OneToOne(orphanRemoval = true)
  @JoinColumn(name = "member_id", nullable = false)
  private Member member;
}
