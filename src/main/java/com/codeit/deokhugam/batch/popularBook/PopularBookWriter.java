package com.codeit.deokhugam.batch.popularBook;

import com.codeit.deokhugam.domain.entity.PopularBook;
import com.codeit.deokhugam.repository.book.PopularBookRepository;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PopularBookWriter implements ItemWriter<PopularBook> {

  private final PopularBookRepository popularBookRepository;

  @Override
  public void write(Chunk<? extends PopularBook> chunk) throws Exception {

    List<? extends PopularBook> items = chunk.getItems();

    if (items.isEmpty()) {
      return;
    }

    popularBookRepository.deleteAllInBatch();

    Map<String, List<PopularBook>> groupedByPeriod = items.stream()
        .collect(Collectors.groupingBy(PopularBook::getPeriod));

    groupedByPeriod.forEach((period, books) -> {
      List<PopularBook> sortedBooks = books.stream()
          .sorted(Comparator.comparing(PopularBook::getPeriod).reversed())
          .toList();

      AtomicInteger counter = new AtomicInteger(1);

      sortedBooks.forEach(book -> {
        int currentRank = counter.getAndIncrement();

        book.setRank((short) currentRank);
      });

      popularBookRepository.saveAll(sortedBooks);
    });
  }
}
