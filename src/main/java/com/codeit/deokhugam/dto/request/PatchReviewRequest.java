package com.codeit.deokhugam.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@AllArgsConstructor
@Getter
@Builder
public class PatchReviewRequest {

  @NotBlank(message = "content를 입력해 주세요")
  private final String content;
  @Min(value = 1, message = "평점은 1 이상입니다.")
  @Max(value = 5, message = "평점은 5이하입니다.")
  private final short rating;

}
