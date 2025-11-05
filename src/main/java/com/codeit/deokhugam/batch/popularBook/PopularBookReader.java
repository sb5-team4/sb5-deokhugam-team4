package com.codeit.deokhugam.batch.popularBook;

import com.codeit.deokhugam.batch.popularBook.dto.PopularBookDto;
import com.codeit.deokhugam.repository.book.BookRepository;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemReader;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class PopularBookReader implements ItemReader<PopularBookDto> {

  private static final ZoneId ZONE_KOREA = ZoneId.of("Asia/Seoul");
  private final BookRepository bookRepository;
  private Iterator<PopularBookDto> bookIterator;

  /**
   * 데이터 한 건씩 읽어서 반환하는 메서드, null이 리턴되면 배치 종료
   */
  @Override
  public PopularBookDto read() {
    if (bookIterator == null) {
      List<PopularBookDto> allBooks = fetchAllPeriodBooks();
      log.info("===== 총 {}개의 인기 도서 데이터를 읽어왔습니다 =====", allBooks.size());
      assignRanks(allBooks);
      bookIterator = allBooks.iterator();
    }
    // Iterator로 하나씩 반환 (null 반환 시 배치 종료)
    return bookIterator.hasNext() ? bookIterator.next() : null;
  }

  private List<PopularBookDto> fetchAllPeriodBooks() {
    List<PopularBookDto> allBooks = new ArrayList<>();

    // 한국 시간 기준으로 현재 시간 가져오기
    ZonedDateTime nowKST = ZonedDateTime.now(ZONE_KOREA);
    log.info("배치 실행 시간 (KST): {}", nowKST);

    // DAILY 조회 - 한국 시간 기준 어제 00:00 ~ 오늘 00:00
    LocalDate yesterday = nowKST.toLocalDate().minusDays(1);
    LocalDate today = nowKST.toLocalDate();

    Instant dailyStart = yesterday.atStartOfDay(ZONE_KOREA).toInstant();
    Instant dailyEnd = today.atStartOfDay(ZONE_KOREA).toInstant();

    log.info("DAILY 기간 (KST 기준): {} ~ {}", dailyStart, dailyEnd);

    List<PopularBookDto> dailyBooks = bookRepository
        .findPopularBooksForPeriod(dailyStart, dailyEnd);

    log.info("DAILY 인기 도서: {}개", dailyBooks.size());
    dailyBooks.forEach(book -> book.setPeriod("DAILY"));
    allBooks.addAll(dailyBooks);

    // WEEKLY 조회 - 한국 시간 기준 7일 전 00:00 ~ 오늘 00:00
    LocalDate weekAgo = today.minusDays(7);
    Instant weeklyStart = weekAgo.atStartOfDay(ZONE_KOREA).toInstant();
    Instant weeklyEnd = today.atStartOfDay(ZONE_KOREA).toInstant();

    log.info("WEEKLY 기간 (KST 기준): {} ~ {}", weeklyStart, weeklyEnd);

    List<PopularBookDto> weeklyBooks = bookRepository
        .findPopularBooksForPeriod(weeklyStart, weeklyEnd);

    log.info("WEEKLY 인기 도서: {}개", weeklyBooks.size());
    weeklyBooks.forEach(book -> book.setPeriod("WEEKLY"));
    allBooks.addAll(weeklyBooks);

    // MONTHLY 조회 - 한국 시간 기준 30일 전 00:00 ~ 오늘 00:00
    LocalDate monthAgo = today.minusDays(30);
    Instant monthlyStart = monthAgo.atStartOfDay(ZONE_KOREA).toInstant();
    Instant monthlyEnd = today.atStartOfDay(ZONE_KOREA).toInstant();

    log.info("MONTHLY 기간 (KST 기준): {} ~ {}", monthlyStart, monthlyEnd);

    List<PopularBookDto> monthlyBooks = bookRepository
        .findPopularBooksForPeriod(monthlyStart, monthlyEnd);

    log.info("MONTHLY 인기 도서: {}개", monthlyBooks.size());
    monthlyBooks.forEach(book -> book.setPeriod("MONTHLY"));
    allBooks.addAll(monthlyBooks);

    // ALL_TIME 조회
    Instant allTimeStart = Instant.EPOCH;
    Instant allTimeEnd = nowKST.toInstant();

    log.info("ALL_TIME 기간: {} ~ {}", allTimeStart, allTimeEnd);

    List<PopularBookDto> allTimeBooks = bookRepository
        .findPopularBooksForPeriod(allTimeStart, allTimeEnd);

    log.info("ALL_TIME 인기 도서: {}개", allTimeBooks.size());
    allTimeBooks.forEach(book -> book.setPeriod("ALL_TIME"));
    allBooks.addAll(allTimeBooks);

    return allBooks;
  }

  /**
   * 기간별로 그룹화하여 순위 부여
   *
   * @param books 전체 인기 도서 목록 (이미 정렬됨)
   */
  private void assignRanks(List<PopularBookDto> books) {
    Map<String, List<PopularBookDto>> groupedByPeriod = books.stream()
        .collect(Collectors.groupingBy(PopularBookDto::getPeriod));

    groupedByPeriod.forEach((period, periodBooks) -> {
      periodBooks.sort((a, b) ->
          b.calculateScore().compareTo(a.calculateScore())
      );

      AtomicInteger rank = new AtomicInteger(1);
      periodBooks.forEach(book ->
          book.setRank((short) rank.getAndIncrement())
      );

      log.info("{} 기간 순위 부여 완료: {}개 도서", period, periodBooks.size());
    });
  }
}