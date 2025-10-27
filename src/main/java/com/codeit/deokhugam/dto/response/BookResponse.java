package com.codeit.deokhugam.dto.response;

import com.codeit.deokhugam.domain.entity.Book;
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
public class BookResponse {

  private Long id;                  // 도서 id
  private String title;             // 제목
  private String author;            // 저자
  private String description;       // 설명
  private String publisher;         // 출판사
  private LocalDate publishedDate;  // 출판일
  private String isbn;              // ISBN
  private String thumbnailUrl;      // 썸네일 URL
  private Integer reviewCount;      // 리뷰수
  private BigDecimal rating;        // 평점 (0.00 ~ 5.00)
  private Instant createdAt;        // 생성 시간
  private Instant updatedAt;        // 수정 시간

  public static BookResponse from(Book book) {
    return BookResponse.builder()
        .id(book.getId())
        .title(book.getTitle())
        .author(book.getAuthor())
        .description(book.getDescription())
        .publisher(book.getPublisher())
        .publishedDate(book.getPublishedDate())
        .isbn(book.getIsbn())
        .thumbnailUrl(book.getThumbnailUrl())
        .reviewCount(book.getReviewCount())
        .rating(book.getRating())
        .createdAt(book.getCreatedAt())
        .updatedAt(book.getUpdatedAt())
        .build();
  }
}