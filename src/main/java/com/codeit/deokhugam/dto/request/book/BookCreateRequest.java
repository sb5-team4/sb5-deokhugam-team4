package com.codeit.deokhugam.dto.request.book;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookCreateRequest {

  @NotBlank(message = "도서 제목은 필수입니다.")
  @Size(max = 255, message = "도서 제목은 255자를 초과할 수 없습니다.")
  private String title;

  @NotBlank(message = "저자 이름은 필수입니다.")
  @Size(max = 255, message = "저자 이름은 255자를 초과할 수 없습니다.")
  private String author;

  @NotBlank(message = "도서 설명은 필수입니다.")
  @Size(max = 1000, message = "도서 설명은 1000자를 초과할 수 없습니다.")
  private String description;

  @NotBlank(message = "출판사는 필수입니다.")
  @Size(max = 255, message = "출판사는 255자를 초과할 수 없습니다.")
  private String publisher;

  @NotNull(message = "출판일은 필수입니다.")
  private LocalDate publishedDate;

  @Pattern(regexp = "^\\d{13}$", message = "ISBN은 13자리 숫자여야 합니다.")
  private String isbn;

  @Size(max = 500, message = "썸네일 URL은 500자를 초과할 수 없습니다.")
  private String thumbnailUrl;

}
