package com.codeit.deokhugam.dto.result.book;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookUpdateResult {

  private Long id;                  // 도서 ID
  private String title;             // 수정된 제목
  private String author;            // 수정된 저자
  private String description;       // 수정된 설명
  private String publisher;         // 수정된 출판사
  private LocalDate publishedDate;  // 수정된 출판일
  private String isbn;              // ISBN
  private String thumbnailUrl;      // 썸네일 URL
  private Integer reviewCount;      // 리뷰 수
  private BigDecimal rating;        // 평점
  private Instant createdAt;        // 생성 시간
  private Instant updatedAt;        // 수정 시간
}