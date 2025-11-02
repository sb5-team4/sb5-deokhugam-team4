package com.codeit.deokhugam.dto.command;

import java.time.Instant;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.springframework.data.domain.Sort.Direction;

@AllArgsConstructor
@Getter
@Builder
public class GetNotificationCommand {

  private final Long userId;
  private final Direction direction;
  private final Instant cursor;
  private final Instant after;
  private final Integer limit;

  public static GetNotificationCommand from(
      Long authorId,
      Direction direction,
      Instant cursor,
      Instant after,
      Integer limit

  ) {

    return GetNotificationCommand.builder()
        .userId(authorId)
        .direction(direction)
        .cursor(cursor)
        .after(after)
        .limit(limit)
        .build();
  }
}
