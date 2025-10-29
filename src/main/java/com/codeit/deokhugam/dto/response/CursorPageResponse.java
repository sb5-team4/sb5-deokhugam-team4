package com.codeit.deokhugam.dto.response;

import java.time.Instant;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@AllArgsConstructor
@Getter
@Builder
public class CursorPageResponse<T, S> {

  private final List<T> content;
  private final S nextCursor;
  private final Instant nextAfter;
  private final Integer size;
  private final Long totalElements;
  private final Boolean hasNext;

  public static <T, S> CursorPageResponse<T, S> from(
      List<T> content,
      S nextCursor,
      Instant nextAfter,
      Integer size,
      Long totalElements,
      Boolean hasNext

  ) {
    return CursorPageResponse.<T, S>builder()
        .content(content)
        .nextCursor(nextCursor)
        .nextAfter(nextAfter)
        .size(size)
        .totalElements(totalElements)
        .hasNext(hasNext)
        .build();
  }

}
