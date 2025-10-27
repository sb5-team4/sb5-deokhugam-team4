package com.codeit.deokhugam.service;

import com.codeit.deokhugam.domain.enums.Period;
import java.math.BigDecimal;
import java.time.Instant;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.springframework.data.domain.Sort.Direction;

@AllArgsConstructor
@Getter
@Builder
public class GetPopularReviewsCommand {

  private final Period period;
  private final Direction direction;
  private final BigDecimal cursor;
  private final Instant after;
  private final Integer limit;
}
