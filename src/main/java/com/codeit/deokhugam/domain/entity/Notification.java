package com.codeit.deokhugam.domain.entity;

import com.codeit.deokhugam.domain.entity.base.BaseUpdatableEntity;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;


@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Entity
public class Notification extends BaseUpdatableEntity {

  @Column(nullable = false)
  private String content;
  @Column(nullable = false)
  private boolean confirmed;
  @Column(nullable = false)
  private boolean deleted;

  @ManyToOne(cascade = CascadeType.REMOVE)
  @JoinColumn(name = "member_id", nullable = false)
  private Member member;

  @ManyToOne(cascade = CascadeType.REMOVE)
  @JoinColumn(name = "review_id", nullable = false)
  private Review review;

  public void read(boolean confirmed) {
    this.confirmed = confirmed;
  }
}
