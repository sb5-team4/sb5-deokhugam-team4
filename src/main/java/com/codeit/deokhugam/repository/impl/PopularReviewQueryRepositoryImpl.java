package com.codeit.deokhugam.repository.impl;

import static org.springframework.data.domain.Sort.Direction.ASC;

import com.codeit.deokhugam.domain.entity.PopularReview;
import com.codeit.deokhugam.domain.entity.QPopularReview;
import com.codeit.deokhugam.domain.enums.Period;
import com.codeit.deokhugam.dto.result.PaginatedResult;
import com.codeit.deokhugam.repository.PopularReviewQueryRepository;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort.Direction;

@RequiredArgsConstructor
public class PopularReviewQueryRepositoryImpl implements PopularReviewQueryRepository {

  private final JPAQueryFactory queryFactory;
  private static final QPopularReview pr = QPopularReview.popularReview;


  @Override
  public PaginatedResult<PopularReview, Long> searchWithCursor(
      Period period,
      Direction direction,
      int limit,
      Long cursor,
      Instant after
  ) {

    // 1. where 조건 빌드
    BooleanBuilder where = new BooleanBuilder();

    if (period != null) {
      where.and(pr.period.eq(period.name()));
    }

    // 2. 정렬 조건
    OrderSpecifier<?> order;
    boolean isAsc = direction.equals(ASC);

    if (isAsc) {
      order = pr.createdAt.asc();
      order = pr.rank.asc();
    } else {
      order = pr.createdAt.asc();
      order = pr.rank.desc();
    }

    if (cursor != null && isAsc) {
      where.and(pr.rank.gt(cursor));
//          .or(pr.createdAt.goe(after)); rank는 Unique하기 때문에 보조커서 X
    } else if (cursor != null) {
      where.and(pr.rank.lt(cursor));
//          .or(pr.createdAt.goe(after)));
    }

    List<PopularReview> contents = queryFactory.selectFrom(pr)
        .where(where.and(pr.ordered.isTrue()))
        .where(pr.review.deleted.isFalse())
        .distinct()
        .join(pr.review).fetchJoin()
        .join(pr.review.book).fetchJoin()
        .join(pr.review.member).fetchJoin()
        .orderBy(order)
        .limit(limit + 1)
        .fetch();

    // 반환될 컨텐츠
    List<PopularReview> pageContent = contents.size() > limit
        ? contents.subList(0, limit)
        : contents;
    Optional<PopularReview> last = pageContent.isEmpty()
        ? Optional.empty()
        : Optional.of(pageContent.get(pageContent.size() - 1));

    Long nextCursor = last.map(PopularReview::getRank).orElse(null);
    Instant nextAfter = last.map(PopularReview::getCreatedAt).orElse(null);

    Long count = queryFactory
        .select(pr.count())
        .from(pr)
        .where(where.and(pr.ordered.isTrue()))
        .where(pr.review.deleted.isFalse())
        .fetchOne();

    return PaginatedResult.<PopularReview, Long>builder()
        .content(pageContent)
        .nextCursor(nextCursor)
        .nextAfter(nextAfter)
        .size(pageContent.size())
        .totalElements(count)
        .hasNext(contents.size() > limit)
        .build();
  }
}
