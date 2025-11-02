package com.codeit.deokhugam.repository;

import com.codeit.deokhugam.domain.entity.Book;
import java.time.Instant;
import java.util.List;

public interface BookQueryRepository {

  List<Book> findBooksWithCursor(
      String keyword,
      String orderBy,
      String direction,
      String cursor,
      Instant after,
      int limit
  );

  long countBooksWithCursor(String keyword);

}
