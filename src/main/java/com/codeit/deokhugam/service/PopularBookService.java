package com.codeit.deokhugam.service;

import com.codeit.deokhugam.domain.entity.PopularBook;
import com.codeit.deokhugam.dto.command.PopularBookCommand;
import com.codeit.deokhugam.dto.result.PopularBookListResult;
import com.codeit.deokhugam.dto.result.PopularBookListResult.PopularBookResult;
import com.codeit.deokhugam.mapper.PopularBookMapper;
import com.codeit.deokhugam.repository.PopularBookRepository;
import java.time.Instant;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PopularBookService {

  private final PopularBookRepository popularBookRepository;
  private final PopularBookMapper popularBookMapper;

  @Transactional(readOnly = true)
  public PopularBookListResult getPopularBooks(PopularBookCommand command) {
    String period = command.getPeriod();
    String direction = command.getDirection();
    String cursor = command.getCursor();
    Instant after = command.getAfter();
    Integer limit = command.getLimit();

    List<PopularBook> popularBooks = popularBookRepository.findPopularBooksWithCursor(
        period,
        direction,
        cursor,
        after,
        limit
    );

    boolean hasNext = popularBooks.size() > limit;

    List<PopularBook> content = hasNext
        ? popularBooks.subList(0, limit)
        : popularBooks;

    List<PopularBookResult> popularBookResults = content.stream()
        .map(popularBookMapper::toPopularBookResult)
        .toList();

    String nextCursor = null;
    Instant nextAfter = null;

    if (hasNext && !content.isEmpty()) {
      PopularBook lastBook = content.get(content.size() - 1);
      nextCursor = String.valueOf(lastBook.getRank());
      nextAfter = lastBook.getCreatedAt();
    }

    Long totalElements = popularBookRepository.countPopularBooksWithCursor(period);

    return PopularBookListResult.builder()
        .content(popularBookResults)
        .nextCursor(nextCursor)
        .nextAfter(nextAfter)
        .size(content.size())
        .totalElements(totalElements)
        .hasNext(hasNext)
        .build();
  }
}
