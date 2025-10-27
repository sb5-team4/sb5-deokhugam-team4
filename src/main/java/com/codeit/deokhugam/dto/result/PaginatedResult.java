package com.codeit.deokhugam.dto.result;

import java.time.Instant;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@AllArgsConstructor
@Getter
@Builder
public class PaginatedResult<T, S> {

  private final List<T> content;
  private final S nextCursor;
  private final Long nextIdAfter;
  private final Instant nextAfter;
  private final int size;
  private final Long totalElements;
  private final Boolean hasNext;
}
