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

  @NotNull(message = "bookId는 필수 값입니다.")
  private final Long bookId;
  @NotNull(message = "userId는 필수 값입니다.")
  private final Long userId;
  @NotBlank(message = "리뷰 내용을 입력해주세요.")
  private final String content;
  @Min(value = 1, message = "평점은 최소 1점 이상이어야 합니다.")
  @Max(value = 5, message = "평점은 최대 5점 이하이어야 합니다.")
  private final int rating;
}
