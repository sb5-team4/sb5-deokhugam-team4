package com.codeit.deokhugam.dto.result;

import com.codeit.deokhugam.domain.enums.Period;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@AllArgsConstructor
@Getter
@Builder
public class PopularReviewResult {

  private final Long id;
  private final Long reviewId;
  private final Long bookId;
  private final String bookTitle;
  private final String bookThumbnailUrl;
  private final Long userId;
  private final String userNickname;
  private final String reviewContent;
  private final Short reviewRating;
  private final Period period;
  private final OffsetDateTime createdAt;
  private final Long rank;
  private final BigDecimal score;
  private final Long likeCount;
  private final Long commentCount;

}
