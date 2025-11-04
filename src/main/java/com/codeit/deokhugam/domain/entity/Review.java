package com.codeit.deokhugam.domain.entity;

import com.codeit.deokhugam.domain.entity.base.BaseUpdatableEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Version;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@SuperBuilder(toBuilder = true)
@ToString
@Entity
public class Review extends BaseUpdatableEntity {

  @Column(nullable = false)
  private boolean deleted;
  @Column(nullable = false)
  private Short rating;
  @Column(nullable = false)
  private String content;

  @Builder.Default
  @Column(nullable = false, columnDefinition = "bigint default 0")
  private Long likeCount = 0L;

  @Builder.Default
  @Column(nullable = false, columnDefinition = "bigint default 0")
  private Long commentCount = 0L;

  @ManyToOne
  @JoinColumn(name = "book_id", nullable = false)
  private Book book;
  @ManyToOne
  @JoinColumn(name = "member_id", nullable = false)
  private Member member;

  @Version
  @Column(nullable = false)
  private Long version;

  public void updateReview(String newContent, short newRating) {
    content = newContent;
    rating = newRating;
  }
}
