package com.codeit.deokhugam.dto.command.book;

import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BookUpdateCommand {

  private String title;             // 제목
  private String author;            // 저자
  private String description;       // 설명
  private String publisher;         // 출판사
  private LocalDate publishedDate;  // 출판일

}
