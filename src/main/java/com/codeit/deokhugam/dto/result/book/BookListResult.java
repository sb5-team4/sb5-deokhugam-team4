package com.codeit.deokhugam.dto.result.book;


import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookListResult {

  private List<BookResult> content;
  private String nextCursor;
  private Instant nextAfter;
  private int size;
  private long totalElements;
  private boolean hasNext;

  @Getter
  @Builder
  @NoArgsConstructor
  @AllArgsConstructor
  public static class BookResult {

    private Long id;
    private String title;
    private String author;
    private String description;
    private String publisher;
    private LocalDate publishedDate;
    private String isbn;
    private String thumbnailUrl;
    private int reviewCount;
    private BigDecimal rating;
    private boolean deleted;
    private Instant createdAt;
    private Instant updatedAt;
  }

}
