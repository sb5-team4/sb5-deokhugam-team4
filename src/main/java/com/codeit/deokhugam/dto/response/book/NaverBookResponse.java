package com.codeit.deokhugam.dto.response.book;

import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder(toBuilder = true)  // toBuilder 추가!
@AllArgsConstructor
@NoArgsConstructor
public class NaverBookResponse {

  private String title;
  private String author;
  private String description;
  private String publisher;
  private LocalDate publishedDate;
  private String isbn;
  private String thumbnailUrl;
  private String thumbnailImage;
}