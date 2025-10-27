package com.codeit.deokhugam.dto.result;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@AllArgsConstructor
@Getter
@Builder
public class GetPopularReviewsResult {

  private final List<PopularReviewResult> popularReviews;
  private final BigDecimal nextCursor;
  private final Instant nextAfter;
  private final Integer size;
  private final Long totalElements;
  private final Boolean hasNext;

}
