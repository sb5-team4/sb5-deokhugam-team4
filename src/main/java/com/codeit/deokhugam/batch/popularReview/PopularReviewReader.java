package com.codeit.deokhugam.batch.popularReview;

import com.codeit.deokhugam.domain.entity.Review;
import com.codeit.deokhugam.repository.ReviewRepository;
import java.util.Iterator;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.item.ItemReader;
import org.springframework.stereotype.Component;

@Component
@JobScope
@RequiredArgsConstructor
public class PopularReviewReader implements ItemReader<Review> {

  private final ReviewRepository reviewRepository;

  private Iterator<Review> reviewIterator;

  @Override
  public Review read() throws Exception {
    // 처음 호출될 때만 DB 조회
    if (reviewIterator == null) {
      List<Review> reviews = reviewRepository.findAllByDeletedIsFalse();
      reviewIterator = reviews.iterator();
    }

    // Iterator 에서 한 건씩 반환
    if (reviewIterator.hasNext()) {
      return reviewIterator.next();
    } else {
      // 더 이상 읽을 데이터가 없으면 null 반환 → Batch 종료 신호
      return null;
    }
  }
}
