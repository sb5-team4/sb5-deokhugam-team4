package com.codeit.deokhugam.repository;

import com.codeit.deokhugam.domain.entity.PopularBook;
import java.time.Instant;
import java.util.List;

public interface PopularBookQueryRepository {

  List<PopularBook> findPopularBooksWithCursor(
      String period,
      String direction,
      String cursor,
      Instant after,
      Integer limit
  );

  Long countPopularBooksWithCursor(String period);

}
