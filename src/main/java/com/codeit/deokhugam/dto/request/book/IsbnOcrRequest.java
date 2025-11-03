package com.codeit.deokhugam.dto.request.book;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IsbnOcrRequest {

  @NotNull(message = "이미지 파일은 필수입니다.")
  private MultipartFile image;

}
