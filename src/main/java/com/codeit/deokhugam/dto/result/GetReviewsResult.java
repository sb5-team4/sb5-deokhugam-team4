package com.codeit.deokhugam.dto.result;

import java.time.Instant;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@AllArgsConstructor
@Getter
@Builder
public class GetReviewsResult {

  private final List<GetReviewOneResult> reviews;
  private final String nextCursor;
  private final Instant nextAfter;
  private final Integer size;
  private final Long totalElements;
  private final Boolean hasNext;

  public static GetReviewsResult from(
      List<GetReviewOneResult> reviews,
      String nextCursor,
      Instant nextAfter,
      Integer size,
      Long totalElements,
      Boolean hasNext) {

    return GetReviewsResult.builder()
        .reviews(reviews)
        .nextCursor(nextCursor)
        .nextAfter(nextAfter)
        .size(size)
        .totalElements(totalElements)
        .hasNext(hasNext)
        .build();
  }
}
