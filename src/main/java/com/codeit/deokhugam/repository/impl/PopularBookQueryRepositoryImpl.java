package com.codeit.deokhugam.repository.impl;

import static com.codeit.deokhugam.domain.entity.QBook.book;
import static com.codeit.deokhugam.domain.entity.QPopularBook.popularBook;

import com.codeit.deokhugam.domain.entity.PopularBook;
import com.codeit.deokhugam.repository.PopularBookQueryRepository;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class PopularBookQueryRepositoryImpl implements PopularBookQueryRepository {

  private final JPAQueryFactory queryFactory;

  @Override
  public List<PopularBook> findPopularBooksWithCursor(
      String period, String direction,
      String cursor, Instant after, Integer limit) {

    List<OrderSpecifier<?>> orderSpecifiers = new ArrayList<>();

    if ("DESC".equalsIgnoreCase(direction)) {
      orderSpecifiers.add(popularBook.rank.desc());
    } else {
      orderSpecifiers.add(popularBook.rank.asc());
    }

    orderSpecifiers.add(popularBook.createdAt.asc());

    BooleanExpression cursorCondition = createCursorCondition(
        cursor, after, direction
    );

    BooleanExpression periodCondition = popularBook.period.eq(period);

    return queryFactory
        .selectFrom(popularBook)
        .join(popularBook.book, book).fetchJoin()
        .where(periodCondition, cursorCondition)
        .orderBy(orderSpecifiers.toArray(new OrderSpecifier[0]))
        .limit(limit + 1)
        .fetch();
  }

  @Override
  public Long countPopularBooksWithCursor(String period) {
    return queryFactory
        .select(popularBook.count())
        .from(popularBook)
        .where(popularBook.period.eq(period))
        .fetchOne();
  }

  private BooleanExpression createCursorCondition(
      String cursor, Instant after, String direction) {

    if (cursor == null && after == null) {
      return null;
    }

    Short rankCursor = cursor != null ? Short.parseShort(cursor) : null;

    if ("ASC".equalsIgnoreCase(direction)) {
      if (rankCursor != null && after != null) {
        return popularBook.rank.gt(rankCursor)
            .or(popularBook.rank.eq(rankCursor).and(popularBook.createdAt.gt(after)));
      } else if (rankCursor != null) {
        return popularBook.rank.gt(rankCursor);
      } else {
        return popularBook.createdAt.gt(after);
      }
    } else {
      if (rankCursor != null && after != null) {
        return popularBook.rank.lt(rankCursor)
            .or(popularBook.rank.eq(rankCursor).and(popularBook.createdAt.gt(after)));
      } else if (rankCursor != null) {
        return popularBook.rank.lt(rankCursor);
      } else {
        return popularBook.createdAt.gt(after);
      }
    }
  }
}