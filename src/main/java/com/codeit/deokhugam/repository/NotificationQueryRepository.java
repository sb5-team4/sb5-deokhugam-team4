package com.codeit.deokhugam.repository;

import com.codeit.deokhugam.domain.entity.Notification;
import com.codeit.deokhugam.dto.result.PaginatedResult;
import java.time.Instant;
import org.springframework.data.domain.Sort.Direction;

public interface NotificationQueryRepository {

  PaginatedResult<Notification, Instant> searchWithCursor(
      Long authorId,
      Direction direction,
      Instant cursor, // nullable
      Instant after, // nullable
      Integer limit
  );

}
