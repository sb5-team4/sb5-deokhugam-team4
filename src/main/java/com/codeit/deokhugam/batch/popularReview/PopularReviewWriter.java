package com.codeit.deokhugam.batch.popularReview;

import com.codeit.deokhugam.domain.entity.PopularReview;
import com.codeit.deokhugam.repository.PopularReviewRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PopularReviewWriter implements ItemWriter<List<PopularReview>> {

  private final PopularReviewRepository popularReviewRepository;
  private final RankCounter rankCounter;

  @Override
  public void write(Chunk<? extends List<PopularReview>> chunk) throws Exception {

    List<PopularReview> items = chunk.getItems().stream().flatMap(List::stream).toList();
    items.forEach(pr -> pr.setRank(rankCounter.nextRank()));
    popularReviewRepository.saveAll(items);

  }
}
