package com.codeit.deokhugam.dto.result;

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
public class PopularBookListResult {

  private List<PopularBookResult> content;
  private String nextCursor;
  private Instant nextAfter;
  private Integer size;
  private Long totalElements;
  private Boolean hasNext;

  @Getter
  @Builder
  @NoArgsConstructor
  @AllArgsConstructor
  public static class PopularBookResult {

    private Long id;        // popularBook Id
    private Long bookId;    // book Id
    private String title;
    private String author;
    private String thumbnailUrl;
    private String period;
    private Long rank;
    private Long reviewCount;
    private BigDecimal rating;
    private BigDecimal score;
    private Instant createdAt;

  }

}
