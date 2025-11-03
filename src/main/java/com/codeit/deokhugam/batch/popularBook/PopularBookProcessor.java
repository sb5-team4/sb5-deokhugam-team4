package com.codeit.deokhugam.batch.popularBook;

import com.codeit.deokhugam.batch.common.BatchCustomException;
import com.codeit.deokhugam.batch.common.BatchErrorCode;
import com.codeit.deokhugam.batch.popularBook.dto.PopularBookDto;
import com.codeit.deokhugam.domain.entity.Book;
import com.codeit.deokhugam.domain.entity.PopularBook;
import com.codeit.deokhugam.repository.book.BookRepository;
import java.math.BigDecimal;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PopularBookProcessor implements ItemProcessor<PopularBookDto, PopularBook> {

  private final BookRepository bookRepository;

  @Override
  public PopularBook process(PopularBookDto dto) throws Exception {

    Long bookId = dto.getBookId();

    Book book = bookRepository.findById(bookId)
        .orElseThrow(
            () -> new BatchCustomException(BatchErrorCode.BOOK_NOT_FOUND_IN_BATCH, bookId));

    BigDecimal score = dto.calculateScore();

    PopularBook popularBook = PopularBook.builder()
        .book(book)
        .period(dto.getPeriod())
        .rank((short) 0)
        .score(score)
        .reviewCount(dto.getReviewCount())
        .rating(dto.getAverageRating())
        .build();

    return popularBook;
  }
}
