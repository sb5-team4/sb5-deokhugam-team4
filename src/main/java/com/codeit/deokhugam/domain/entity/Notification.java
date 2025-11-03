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
import lombok.Setter;


@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
@Entity
public class Notification extends BaseUpdatableEntity {

  @Column(nullable = false)
  private String content;
  @Column(nullable = false)
  private boolean confirmed;
  @Column(nullable = false)
  private boolean deleted;

  @ManyToOne
  @JoinColumn(name = "member_id", nullable = false)
  private Member member;

  @ManyToOne
  @JoinColumn(name = "review_id", nullable = false)
  private Review review;

  @ManyToOne(cascade = CascadeType.REMOVE)
  @JoinColumn(name = "comment_id", nullable = false)
  private Comment comment;

  public void read(boolean confirmed) {
    this.confirmed = confirmed;
  }
}
