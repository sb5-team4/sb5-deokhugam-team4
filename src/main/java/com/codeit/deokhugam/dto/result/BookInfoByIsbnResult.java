package com.codeit.deokhugam.dto.result;

import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookInfoByIsbnResult {

  private String title;            // 도서 제목
  private String author;           // 저자 이름
  private String description;      // 도서 소개
  private String publisher;        // 출판사
  private LocalDate publishedDate; // 출판일
  private String isbn;             // ISBN 번호
  private String thumbnailImage;   // 썸네일 이미지 URL

}
