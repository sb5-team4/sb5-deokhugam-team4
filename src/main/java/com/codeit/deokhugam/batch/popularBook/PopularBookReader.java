package com.codeit.deokhugam.batch.popularBook;

import com.codeit.deokhugam.batch.popularBook.dto.PopularBookDto;
import com.codeit.deokhugam.repository.book.BookRepository;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.item.ItemReader;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PopularBookReader implements ItemReader<PopularBookDto> {

  private final BookRepository bookRepository;
  private Iterator<PopularBookDto> bookIterator;

  /**
   * 데이터 한 건씩 읽어서 반환하는 메서드 null이 리턴되면 배치 종료
   */
  @Override
  public PopularBookDto read() {
    if (bookIterator == null) {
      List<PopularBookDto> allBooks = fetchAllPeriodBooks();
      bookIterator = allBooks.iterator();
    }
    if (bookIterator.hasNext()) {
      return bookIterator.next();
    } else {
      return null;
    }
  }

  private List<PopularBookDto> fetchAllPeriodBooks() {
    // DAILY 조회
    Instant now = Instant.now();
    List<PopularBookDto> allBooks = new ArrayList<>();

    Instant dailyStart = now.minus(1, ChronoUnit.DAYS).truncatedTo(ChronoUnit.DAYS);
    Instant dailyEnd = now.truncatedTo(ChronoUnit.DAYS);

    List<PopularBookDto> dailyBooks = bookRepository
        .findPopularBooksForPeriod(dailyStart, dailyEnd);

    dailyBooks.forEach(book -> book.setPeriod("DAILY"));
    allBooks.addAll(dailyBooks);

    // WEEKLY 조회
    Instant weeklyStart = now.minus(7, ChronoUnit.DAYS).truncatedTo(ChronoUnit.DAYS);
    Instant weeklyEnd = now.truncatedTo(ChronoUnit.DAYS);

    List<PopularBookDto> weeklyBooks = bookRepository
        .findPopularBooksForPeriod(weeklyStart, weeklyEnd);

    weeklyBooks.forEach(book -> book.setPeriod("WEEKLY"));
    allBooks.addAll(weeklyBooks);

    // MONTHLY 조회
    Instant monthlyStart = now.minus(30, ChronoUnit.DAYS).truncatedTo(ChronoUnit.DAYS);
    Instant monthlyEnd = now.truncatedTo(ChronoUnit.DAYS);

    List<PopularBookDto> monthlyBooks = bookRepository
        .findPopularBooksForPeriod(monthlyStart, monthlyEnd);

    monthlyBooks.forEach(book -> book.setPeriod("MONTHLY"));
    allBooks.addAll(monthlyBooks);

    // ALL_TIME 조회
    Instant allTimeStart = Instant.EPOCH;
    Instant allTimeEnd = now.truncatedTo(ChronoUnit.DAYS);

    List<PopularBookDto> allTimeBooks = bookRepository
        .findPopularBooksForPeriod(allTimeStart, allTimeEnd);

    allTimeBooks.forEach(book -> book.setPeriod("ALLTIME"));
    allBooks.addAll(allTimeBooks);

    return allBooks;
  }
}
