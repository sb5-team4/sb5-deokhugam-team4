package com.codeit.deokhugam.dto.command;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@AllArgsConstructor
@Getter
@Builder
public class PatchReviewCommand {

  private final Long memberId;
  private final Long reviewId;
  private final String newContent;
  private final short newRating;

}
