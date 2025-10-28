package com.codeit.deokhugam.dto.command;

import com.codeit.deokhugam.domain.enums.ReviewOrderBy;
import java.time.Instant;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.springframework.data.domain.Sort.Direction;

@AllArgsConstructor
@Getter
@Builder
public class GetReviewsCommand {

  private final Long authorId;
  private final Long bookId;
  private final String keyword;
  private final ReviewOrderBy orderBy;
  private final Direction direction;
  private final String cursor;
  private final Instant after;
  private final Integer limit;
  private final Long requestUserId;
  private final Long loginUserId;

  public static GetReviewsCommand from(
      Long authorId,
      Long bookId,
      String keyword,
      ReviewOrderBy orderBy,
      Direction direction,
      String cursor,
      Instant after,
      Integer limit,
      Long requestUserId,
      Long loginUserId
  ) {
    return GetReviewsCommand.builder()
        .authorId(authorId)
        .bookId(bookId)
        .keyword(keyword)
        .orderBy(orderBy)
        .direction(direction)
        .cursor(cursor)
        .after(after)
        .limit(limit)
        .requestUserId(requestUserId)
        .loginUserId(loginUserId)
        .build();
  }

}
