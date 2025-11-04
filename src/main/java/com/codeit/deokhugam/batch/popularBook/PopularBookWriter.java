package com.codeit.deokhugam.batch.popularBook;

import com.codeit.deokhugam.domain.entity.PopularBook;
import com.codeit.deokhugam.repository.book.PopularBookRepository;
import java.util.List;
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

    popularBookRepository.saveAll(items);
  }
}