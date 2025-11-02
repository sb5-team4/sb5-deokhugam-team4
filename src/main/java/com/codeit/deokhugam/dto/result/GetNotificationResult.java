package com.codeit.deokhugam.dto.result;

import com.codeit.deokhugam.domain.entity.Notification;
import java.time.Instant;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@AllArgsConstructor
@Getter
@Builder
public class GetNotificationResult {

  private final List<GetNotificationOneResult> notifications;
  private final Instant nextCursor;
  private final Instant nextAfter;
  private final Integer size;
  private final Long totalElements;
  private final Boolean hasNext;

  public static GetNotificationResult from(
      PaginatedResult<Notification, Instant> entitiesResult,
      List<GetNotificationOneResult> notifications
  ) {
    return GetNotificationResult.builder()
        .notifications(notifications)
        .nextCursor(entitiesResult.getNextCursor())
        .nextAfter(entitiesResult.getNextAfter())
        .size(entitiesResult.getSize())
        .totalElements(entitiesResult.getTotalElements())
        .hasNext(entitiesResult.getHasNext())
        .build();

  }
}
