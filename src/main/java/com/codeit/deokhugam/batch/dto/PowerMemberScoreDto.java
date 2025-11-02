package com.codeit.deokhugam.batch.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@AllArgsConstructor
@Setter
public class PowerMemberScoreDto {

  private Long memberId;
  private long reviewScoreSum;
  private long likeCount;
  private long commentCount;
  private String period;

  // 반드시 이 생성자가 필요
  public PowerMemberScoreDto(Long memberId, Long reviewScoreSum, Long likeCount,
      Long commentCount) {
    this.memberId = memberId;
    this.reviewScoreSum = reviewScoreSum;
    this.likeCount = likeCount;
    this.commentCount = commentCount;
  }

  // period 세팅용 Setter
  public void setPeriod(String period) {
    this.period = period;
  }
}
