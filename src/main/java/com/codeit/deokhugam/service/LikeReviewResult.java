package com.codeit.deokhugam.service;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@AllArgsConstructor
@Getter
@Builder
public class LikeReviewResult {

  private Long reviewId;
  private Long memberId;
  private boolean liked;
}
