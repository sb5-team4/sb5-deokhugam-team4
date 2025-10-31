package com.codeit.deokhugam.dto.request.comment;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommentCreateRequest {

  @NotNull(message = "리뷰 ID는 필수입니다.")
  private Long reviewId;

  @NotNull(message = "사용자 ID는 필수입니다.")
  private Long memberId;

  @NotBlank(message = "댓글 내용은 비워둘 수 없습니다.")
  @Size(min = 1, max = 300, message = "댓글은 1자 이상 300자 이하로 작성해야 합니다.")
  private String content;

}
