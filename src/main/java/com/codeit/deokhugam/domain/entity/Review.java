package com.codeit.deokhugam.domain.entity;

import com.codeit.deokhugam.domain.entity.base.BaseUpdatableEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@SuperBuilder
@ToString
@Entity
public class Review extends BaseUpdatableEntity {

  @Column(nullable = false)
  private boolean deleted;
  @Column(nullable = false)
  private Short rating;
  @Column(nullable = false)
  private String content;

  @Column(nullable = false, columnDefinition = "Long default 0L")
  private Long likeCount = 0L;
  @Column(nullable = false, columnDefinition = "Long default 0L")
  private Long commentCount = 0L;

  @ManyToOne
  @JoinColumn(name = "book_id", nullable = false)
  private Book book;
  @ManyToOne
  @JoinColumn(name = "member_id", nullable = false)
  private Member member;

  public void updateReview(String newContent, short newRating) {
    content = newContent;
    rating = newRating;
  }
}
