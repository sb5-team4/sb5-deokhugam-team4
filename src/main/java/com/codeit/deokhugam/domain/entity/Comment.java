package com.codeit.deokhugam.domain.entity;

import com.codeit.deokhugam.domain.entity.base.BaseUpdatableEntity;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@Getter
@Entity
public class Comment extends BaseUpdatableEntity {

  @Column(nullable = false)
  private String content;

  @Builder.Default
  @Column(nullable = false)
  private boolean deleted = false; //기본값 설정

  // 하나의 리뷰(Review)는 여러 댓글(Comment)을 가짐
  @ManyToOne(fetch = FetchType.LAZY) // 지연 로딩
  @JoinColumn(name = "review_id", nullable = false)
  private Review review;

  // 하나의 사용자(Member)는 여러 댓글(Comment)을 작성함
  @ManyToOne(fetch = FetchType.LAZY) // 지연 로딩
  @JoinColumn(name = "member_id", nullable = false)
  private Member member;

  // Comment가 삭제될 때, 연관된 notifications 리스트도 모두 삭제 셋팅
  @OneToMany(
      mappedBy = "comment",
      cascade = CascadeType.ALL,
      orphanRemoval = true
  )
  @Builder.Default
  private List<Notification> notifications = new ArrayList<>();

  // 댓글 수정 메서드
  public void updateComment(String newContent) {
    content = newContent;
  }

  // 댓글 논리 삭제 메서드
  public void softDelete() {
    if (!this.deleted) {
      this.deleted = true;
    }
  }

}
