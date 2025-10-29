package com.codeit.deokhugam.dto.result;

import java.time.Instant;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@AllArgsConstructor
@Getter
@Builder
public class GetNotificationResult {

  private final List<GetNotificationOneResult> reviews;
  private final Instant nextCursor;
  private final Instant nextAfter;
  private final Integer size;
  private final Long totalElements;
  private final Boolean hasNext;
}
