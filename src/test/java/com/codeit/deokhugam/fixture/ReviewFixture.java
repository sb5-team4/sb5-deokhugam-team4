package com.codeit.deokhugam.fixture;

import com.codeit.deokhugam.domain.entity.Book;
import com.codeit.deokhugam.domain.entity.Member;
import com.codeit.deokhugam.domain.entity.Review;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;

public class ReviewFixture {

  private static final AtomicLong counter = new AtomicLong(1);
  private static final Random random = new Random();

  public static Review createReview(
      Book book,
      Member member
  ) {
    short randomRating = (short) (1 + random.nextInt(5));

    return Review.builder()
        .book(book)
        .member(member)
        .content("content" + counter)
        .rating(randomRating)
        .likeCount(0L)
        .commentCount(0L)
        .deleted(false)
        .build();
  }
}
