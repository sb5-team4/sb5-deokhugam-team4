package com.codeit.deokhugam.dto.request.review;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@AllArgsConstructor
@Getter
@Builder
public class CreateReviewRequest {

  @NotNull
  private final Long bookId;
  @NotNull
  private final Long userId;
  @NotBlank
  private final String content;
  @Min(1)
  @Max(5)
  private final int rating;
}
