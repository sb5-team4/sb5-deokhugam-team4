package com.codeit.deokhugam.dto.response.book;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PopularBookListResponse {

  private List<PopularBookResponse> content;
  private String nextCursor;
  private Instant nextAfter;
  private Integer size;
  private Long totalElements;
  private Boolean hasNext;

  @Getter
  @Builder
  @NoArgsConstructor
  @AllArgsConstructor
  public static class PopularBookResponse {

    private Long id;        // popularBook Id
    private Long bookId;    // book Id
    private String title;
    private String author;
    private String thumbnailUrl;
    private String period;
    private Long rank;
    private BigDecimal score;
    private Long reviewCount;
    private BigDecimal rating;
    private Instant createdAt;
  }

}
