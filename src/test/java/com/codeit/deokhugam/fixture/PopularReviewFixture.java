package com.codeit.deokhugam.fixture;

import com.codeit.deokhugam.domain.entity.PopularReview;
import com.codeit.deokhugam.domain.entity.Review;
import com.codeit.deokhugam.domain.enums.Period;
import java.math.BigDecimal;
import java.util.Random;

public class PopularReviewFixture {


  private static final Random random = new Random();

  public static PopularReview createReview(
      Review review, long rank, Period period
  ) {
    short randomRating = (short) (1 + random.nextInt(5)); // 1~5
    double randomScore = Math.round((random.nextDouble() * 10) * 100) / 100.0;  // 0~10 사이 랜덤(소수2자리)

    return PopularReview.builder()
        .rank(rank)
        .score(BigDecimal.valueOf(randomScore))
        .period(period.name())
        .review(review)
        .ordered(true)
        .build();
  }
}
