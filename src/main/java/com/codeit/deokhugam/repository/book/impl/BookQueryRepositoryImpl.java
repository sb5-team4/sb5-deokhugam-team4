package com.codeit.deokhugam.repository.book.impl;

import com.codeit.deokhugam.domain.entity.Book;
import com.codeit.deokhugam.domain.entity.QBook;
import com.codeit.deokhugam.repository.book.BookQueryRepository;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.annotation.PostConstruct;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import javax.sql.DataSource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

@Slf4j
@Repository
@RequiredArgsConstructor
public class BookQueryRepositoryImpl implements BookQueryRepository {

  private final JPAQueryFactory queryFactory;
  private final QBook book = QBook.book;
  private final DataSource dataSource;

  private boolean isPostgreSQL = false;

  @PostConstruct
  public void init() {
    try {
      String databaseProductName = dataSource.getConnection().getMetaData()
          .getDatabaseProductName();
      isPostgreSQL = databaseProductName.toLowerCase().contains("postgresql");
      log.info("Database detected: {} (isPostgreSQL={})", databaseProductName, isPostgreSQL);
    } catch (Exception e) {
      log.warn("Failed to detect database type, defaulting to H2", e);
      isPostgreSQL = false;
    }
  }

  @Override
  public List<Book> findBooksWithCursor(String keyword, String orderBy, String direction,
      String cursor, Instant after, int limit) {

    BooleanBuilder builder = new BooleanBuilder();
    builder.and(book.deleted.isFalse());
    if (keyword != null && !keyword.isBlank()) {
      builder.and(
          book.title.contains(keyword)
              .or(book.author.contains(keyword))
              .or(book.isbn.contains(keyword))
      );
    }

    if (cursor != null && after != null) {
      BooleanExpression cursorExpression = buildCursorExpression(orderBy, direction, cursor, after);
      builder.and(cursorExpression);
    }

    OrderSpecifier<?>[] orderSpecifiers = buildOrderSpecifiers(orderBy, direction);

    return queryFactory
        .selectFrom(book)
        .where(builder)
        .orderBy(orderSpecifiers)
        .limit(limit + 1)
        .fetch();
  }

  @Override
  public long countBooksWithCursor(String keyword) {
    BooleanBuilder builder = new BooleanBuilder();
    builder.and(book.deleted.isFalse());
    if (keyword != null && !keyword.isBlank()) {
      builder.and(book.title.contains(keyword)
          .or(book.author.contains(keyword))
          .or(book.isbn.contains(keyword))
      );
    }

    Long count = queryFactory
        .select(book.count())
        .from(book)
        .where(builder)
        .fetchOne();

    return count != null ? count : 0L;
  }

  private OrderSpecifier<?>[] buildOrderSpecifiers(
      String orderBy, String direction) {
    boolean isDesc = "DESC".equalsIgnoreCase(direction);

    OrderSpecifier<?> createdAtOrder = isDesc
        ? book.createdAt.desc()
        : book.createdAt.asc();

    OrderSpecifier<?> primaryOrder;

    // title 정렬 시 데이터베이스별 처리
    if ("title".equals(orderBy)) {
      if (isPostgreSQL) {
        // PostgreSQL: COLLATE 적용
        if (isDesc) {
          primaryOrder = Expressions.stringTemplate(
              "CAST({0} AS text) COLLATE \"C\"", book.title
          ).desc();
        } else {
          primaryOrder = Expressions.stringTemplate(
              "CAST({0} AS text) COLLATE \"C\"", book.title
          ).asc();
        }
      } else {
        // H2: 기본 정렬
        primaryOrder = isDesc ? book.title.desc() : book.title.asc();
      }
    } else {
      primaryOrder = switch (orderBy) {
        case "publishedDate" -> isDesc ? book.publishedDate.desc() : book.publishedDate.asc();
        case "rating" -> isDesc ? book.rating.desc() : book.rating.asc();
        case "reviewCount" -> isDesc ? book.reviewCount.desc() : book.reviewCount.asc();
        default -> isDesc ? book.title.desc() : book.title.asc();
      };
    }

    return new OrderSpecifier[]{primaryOrder, createdAtOrder};
  }

  private BooleanExpression buildCursorExpression(
      String orderBy,
      String direction,
      String cursor,
      Instant after) {
    boolean isDesc = "DESC".equalsIgnoreCase(direction);

    return switch (orderBy) {
      case "title" -> buildTitleCursorExpression(cursor, after, isDesc);
      case "publishedDate" -> buildPublishedDateCursorExpression(cursor, after, isDesc);
      case "rating" -> buildRatingCursorExpression(cursor, after, isDesc);
      case "reviewCount" -> buildReviewCountCursorExpression(cursor, after, isDesc);
      default -> buildTitleCursorExpression(cursor, after, isDesc);
    };
  }

  private BooleanExpression buildTitleCursorExpression(
      String cursor,
      Instant after,
      boolean isDesc) {

    if (isPostgreSQL) {
      // PostgreSQL: COLLATE 적용
      if (isDesc) {
        return Expressions.stringTemplate(
                "CAST({0} AS text) COLLATE \"C\"", book.title
            ).lt(cursor)
            .or(book.title.eq(cursor).and(book.createdAt.lt(after)));
      } else {
        return Expressions.stringTemplate(
                "CAST({0} AS text) COLLATE \"C\"", book.title
            ).gt(cursor)
            .or(book.title.eq(cursor).and(book.createdAt.gt(after)));
      }
    } else {
      // H2: 기본 비교
      if (isDesc) {
        return book.title.lt(cursor)
            .or(book.title.eq(cursor).and(book.createdAt.lt(after)));
      } else {
        return book.title.gt(cursor)
            .or(book.title.eq(cursor).and(book.createdAt.gt(after)));
      }
    }
  }

  private BooleanExpression buildPublishedDateCursorExpression(
      String cursor,
      Instant after,
      boolean isDesc) {
    LocalDate cursorDate = LocalDate.parse(cursor);

    if (isDesc) {
      return book.publishedDate.lt(cursorDate)
          .or(book.publishedDate.eq(cursorDate).and(book.createdAt.lt(after)));
    } else {
      return book.publishedDate.gt(cursorDate)
          .or(book.publishedDate.eq(cursorDate).and(book.createdAt.gt(after)));
    }
  }

  private BooleanExpression buildRatingCursorExpression(
      String cursor,
      Instant after,
      boolean isDesc) {
    BigDecimal cursorRating = new BigDecimal(cursor);

    if (isDesc) {
      return book.rating.lt(cursorRating)
          .or(book.rating.eq(cursorRating).and(book.createdAt.lt(after)));
    } else {
      return book.rating.gt(cursorRating)
          .or(book.rating.eq(cursorRating).and(book.createdAt.gt(after)));
    }
  }

  private BooleanExpression buildReviewCountCursorExpression(
      String cursor,
      Instant after,
      boolean isDesc) {
    Long cursorCount = Long.parseLong(cursor);

    if (isDesc) {
      return book.reviewCount.lt(cursorCount)
          .or(book.reviewCount.eq(cursorCount).and(book.createdAt.lt(after)));
    } else {
      return book.reviewCount.gt(cursorCount)
          .or(book.reviewCount.eq(cursorCount).and(book.createdAt.gt(after)));
    }
  }
}