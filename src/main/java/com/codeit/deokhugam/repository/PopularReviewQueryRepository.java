package com.codeit.deokhugam.repository;

import com.codeit.deokhugam.domain.entity.PopularReview;
import com.codeit.deokhugam.domain.enums.Period;
import com.codeit.deokhugam.dto.result.PaginatedResult;
import java.math.BigDecimal;
import java.time.Instant;
import org.springframework.data.domain.Sort.Direction;

public interface PopularReviewQueryRepository {

  PaginatedResult<PopularReview, BigDecimal> searchWithCursor(
      Period period,
      Direction direction,
      int limit,
      BigDecimal cursor, // nullable
      Instant after  // nullable
  );
}
