package com.codeit.deokhugam.repository;


import com.codeit.deokhugam.domain.entity.Review;
import com.codeit.deokhugam.domain.enums.ReviewOrderBy;
import com.codeit.deokhugam.dto.result.PaginatedResult;
import java.time.Instant;
import org.springframework.data.domain.Sort.Direction;

public interface ReviewQueryRepository {

  PaginatedResult<Review, String> searchWithCursor(
      Long authorId, // nullable
      Long bookId, // nullable
      String keyword, // nullable
      Direction direction,
      ReviewOrderBy orderBy,
      String cursor, // nullable
      Instant after, // nullable
      Integer limit,
      Long requestUserId,
      Long loginUserId
  );
}
