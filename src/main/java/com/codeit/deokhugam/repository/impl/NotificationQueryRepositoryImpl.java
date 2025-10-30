package com.codeit.deokhugam.repository.impl;

import com.codeit.deokhugam.domain.entity.Notification;
import com.codeit.deokhugam.domain.entity.QNotification;
import com.codeit.deokhugam.dto.result.PaginatedResult;
import com.codeit.deokhugam.repository.NotificationQueryRepository;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort.Direction;

@RequiredArgsConstructor
public class NotificationQueryRepositoryImpl implements NotificationQueryRepository {

  private final JPAQueryFactory queryFactory;
  private final QNotification nt = QNotification.notification;

  @Override
  public PaginatedResult<Notification, Instant> searchWithCursor(
      Long authorId,
      Direction direction,
      Instant cursor, // nullable
      Instant after, // nullable
      Integer limit
  ) {

    // 1. where 빌드
    BooleanBuilder where = new BooleanBuilder();

    where.and(nt.member.id.eq(authorId));

    // 2. 정렬 조건
    OrderSpecifier<?>[] order;
    boolean isDesc = direction.equals(Direction.DESC);
    if (isDesc) {
      order = new OrderSpecifier[]{nt.createdAt.desc()};
    } else {
      order = new OrderSpecifier[]{nt.createdAt.asc()};
    }

    // 3. 커서
    if (cursor != null && isDesc) {
      where.and(nt.createdAt.lt(cursor));
    } else if (cursor != null) {
      where.and(nt.createdAt.gt(cursor));
    }

    List<Notification> contents = queryFactory.selectFrom(nt)
        .where(where)
        .where(nt.deleted.isFalse())
        .distinct()
        .join(nt.member).fetchJoin()
        .join(nt.review).fetchJoin()
        .orderBy(order)
        .limit(limit + 1)
        .fetch();

    List<Notification> pageContent = contents.size() > limit
        ? contents.subList(0, limit)
        : contents;

    Optional<Notification> last = pageContent.isEmpty()
        ? Optional.empty()
        : Optional.of(pageContent.get(pageContent.size() - 1));

    Instant nextCursor = last.map(Notification::getCreatedAt).orElse(null);
    Instant nextAfter = last.map(Notification::getCreatedAt).orElse(null);

    Long count = queryFactory
        .select(nt.count())
        .from(nt)
        .where(where)
        .where(nt.deleted.isFalse()) // review 가 삭제되지 않은 것만
        .fetchOne();

    return PaginatedResult.<Notification, Instant>builder()
        .content(pageContent)
        .nextCursor(nextCursor)
        .nextAfter(nextAfter)
        .size(pageContent.size())
        .totalElements(count)
        .hasNext(contents.size() > limit)
        .build();
  }
}
