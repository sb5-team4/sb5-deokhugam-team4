package com.codeit.deokhugam.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@AllArgsConstructor
@Getter
@Builder
public class LikeReviewResponse {

  private Long reviewId;
  private Long memberId;
  private boolean liked;
}
