package com.codeit.deokhugam.dto.response.book;

import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class NaverBookResponse {

  private String title;             // 제목
  private String author;            // 저자
  private String description;       // 설명
  private String publisher;         // 출판사
  private LocalDate publishedDate;  // 출판일
  private String isbn;              // ISBN
  private String thumbnailUrl;      // 썸네일 URL

}
