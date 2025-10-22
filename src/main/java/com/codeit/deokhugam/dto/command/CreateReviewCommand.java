package com.codeit.deokhugam.dto.command;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@AllArgsConstructor
@Getter
@Builder
public class CreateReviewCommand {

  private final Long bookId;
  private final Long userId;
  private final String content;
  private final int rating;
}
