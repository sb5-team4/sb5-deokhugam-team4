package com.codeit.deokhugam.repository.impl;

import static com.codeit.deokhugam.domain.enums.ReviewOrderBy.createdAt;
import static org.springframework.data.domain.Sort.Direction.DESC;

import com.codeit.deokhugam.domain.entity.QReview;
import com.codeit.deokhugam.domain.entity.Review;
import com.codeit.deokhugam.domain.enums.ReviewOrderBy;
import com.codeit.deokhugam.dto.result.PaginatedResult;
import com.codeit.deokhugam.repository.ReviewQueryRepository;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort.Direction;

@RequiredArgsConstructor
public class ReviewQueryRepositoryImpl implements ReviewQueryRepository {

  private final JPAQueryFactory queryFactory;
  private final QReview rv = QReview.review;

  @Override
  public PaginatedResult<Review, String> searchWithCursor(
      Long authorId, // nullable
      Long bookId, // nullable
      String keyword, // nullable
      Direction direction,
      ReviewOrderBy orderBy,
      String cursor, // nullable
      Instant after, // nullable
      Integer limit,
      Long requestUserId,
      Long loginUserId) {

    // 1. where 빌드
    BooleanBuilder where = new BooleanBuilder();

    if (authorId != null) {
      where.and(rv.member.id.eq(authorId));
    }
    if (bookId != null) {
      where.and(rv.book.id.eq(bookId));
    }
    if (keyword != null) {
      where.and(
          rv.member.nickname.contains(keyword)
              .or(rv.content.contains(keyword))
              .or(rv.book.title.contains(keyword))
      );
    }

    // 2. 정렬 조건
    OrderSpecifier<?>[] order = null;
    boolean isDESC = direction.equals(DESC); // default == DESC
    boolean isOrderByCreatedAt = orderBy.equals(createdAt);

    if (isDESC && isOrderByCreatedAt) { // 내림 차순, 정렬기준 생성일
      order = new OrderSpecifier[]{rv.createdAt.desc()};
    } else if (isDESC) { // 내림 차순 , 정렬기준 평점
      order = new OrderSpecifier[]{rv.rating.desc(), rv.createdAt.desc()};
    } else if (isOrderByCreatedAt) { // 오름 차순, 정렬기준 생성일
      order = new OrderSpecifier[]{rv.createdAt.asc()};
    } else { // 오름 차순, 정렬 기준 평점
      order = new OrderSpecifier[]{rv.rating.asc(), rv.createdAt.desc()};
    }

    Instant cursorCreatedAt = null;
    Short cursorRating = null;
    if (cursor != null && isOrderByCreatedAt) {
      cursorCreatedAt = Instant.parse(cursor);
    } else if (cursor != null) {
      cursorRating = Short.parseShort(cursor);
    }

    // 3. 커서
    if (cursor != null && isDESC && isOrderByCreatedAt) { // 내림차순, 정렬 기준 생성일
      where.and(rv.createdAt.lt(cursorCreatedAt));
    } else if (cursor != null && isOrderByCreatedAt) { // 오름 차순, 정렬 기준 생성일
      where.and(rv.createdAt.gt(cursorCreatedAt));
    } else if (cursor != null && isDESC) { // 내림 차순, 정렬 기준 평점
      where.and(
          rv.rating.lt(cursorRating) // 평점이 더 낮거나
              .or(
                  rv.rating.eq(cursorRating)
                      .and(rv.createdAt.lt(after)) // 평점 같으면 createdAt이 더 과거(나중 페이지)
              ));
    } else if (cursor != null) { // 오름 차순, 정렬 기준 평점
      where.and(
          rv.rating.gt(cursorRating)
              .or(
                  rv.rating.eq(cursorRating)
                      .and(rv.createdAt.lt(after)) // 평점 같으면 createdAt이 더 과거(나중 페이지)
              ));
    }

    List<Review> contents = queryFactory.selectFrom(rv)
        .where(where)
        .where(rv.deleted.isFalse()) // review 가 삭제되지 않은 것만
        .distinct()
        .join(rv.member).fetchJoin()
        .join(rv.book).fetchJoin()
        .orderBy(order)
        .limit(limit + 1)
        .fetch();

    List<Review> pageContent = contents.size() > limit
        ? contents.subList(0, limit)
        : contents;

    Optional<Review> last = pageContent.isEmpty()
        ? Optional.empty()
        : Optional.of(pageContent.get(pageContent.size() - 1));

    String nextCursor = last.map(rv
        -> isOrderByCreatedAt ? rv.getCreatedAt().toString() : rv.getRating().toString()
    ).orElse(null);
    Instant nextAfter = last.map(Review::getCreatedAt).orElse(null);

    Long count = queryFactory
        .select(rv.count())
        .from(rv)
        .where(where)
        .where(rv.deleted.isFalse()) // review 가 삭제되지 않은 것만
        .fetchOne();

    return PaginatedResult.<Review, String>builder()
        .content(pageContent)
        .nextCursor(nextCursor)
        .nextAfter(nextAfter)
        .size(pageContent.size())
        .totalElements(count)
        .hasNext(contents.size() > limit)
        .build();


  }
}
