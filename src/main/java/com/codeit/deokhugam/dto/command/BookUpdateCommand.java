package com.codeit.deokhugam.dto.command;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class BookUpdateCommand {

  @NotBlank(message = "제목은 비워둘 수 없습니다.")
  private String title;             // 제목

  @NotBlank(message = "저자는 비워둘 수 없습니다.")
  private String author;            // 저자

  private String description;       // 소개

  @NotBlank(message = "출판사는 비워둘 수 없습니다.")
  private String publisher;         // 출판사

  @NotNull(message = "출판일은 비워둘 수 없습니다.")
  private LocalDate publishedDate;  // 출판일

}
