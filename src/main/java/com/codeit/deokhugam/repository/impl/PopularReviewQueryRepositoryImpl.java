package com.codeit.deokhugam.repository.impl;

import com.codeit.deokhugam.domain.entity.PopularReview;
import com.codeit.deokhugam.domain.enums.Period;
import com.codeit.deokhugam.dto.result.PaginatedResult;
import com.codeit.deokhugam.repository.PopularReviewQueryRepository;
import java.math.BigDecimal;
import java.time.Instant;
import org.springframework.data.domain.Sort.Direction;

public class PopularReviewQueryRepositoryImpl implements PopularReviewQueryRepository {

  @Override
  public PaginatedResult<PopularReview, BigDecimal> searchWithCursor(
      Period period,
      Direction direction,
      int limit,
      BigDecimal cursor,
      Instant after
  ) {
    // todo 구현
    return null;
  }
}
