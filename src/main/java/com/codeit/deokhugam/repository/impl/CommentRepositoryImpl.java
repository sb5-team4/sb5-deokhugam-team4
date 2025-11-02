package com.codeit.deokhugam.repository.impl;

import com.codeit.deokhugam.domain.entity.Comment;
import com.codeit.deokhugam.domain.entity.QComment;
import com.codeit.deokhugam.repository.comment.CommentRepositoryCustom;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.time.Instant;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class CommentRepositoryImpl implements CommentRepositoryCustom {

  private final JPAQueryFactory queryFactory; // QuerydslConfig에서 Bean으로 등록됨
  private final QComment qComment = QComment.comment; // Q 클래스 인스턴스

  @Override // 인터페이스 메서드 구현
  public List<Comment> findByReviewId(Long reviewId, String direction, Instant after, Long cursorId ,int limitComment) {

    List<OrderSpecifier<?>> orderSpecifiers = createOrderSpecifiers(direction); // 정렬 조건 생성

    BooleanExpression cursorCondition = createCursorCondition(direction, after, cursorId); // 커서 조건 생성

    return queryFactory
        .selectFrom(qComment)                // SELECT * FROM comment
        .where(
            qComment.review.id.eq(reviewId), // WHERE review_id = ?
            cursorCondition                  // AND (created_at < ? OR created_at > ?)
        )
        .orderBy(orderSpecifiers.toArray(new OrderSpecifier[0])) // ORDER BY created_at [ASC|DESC]
        .limit(limitComment + 1)                                 // LIMIT ? (다음 페이지 확인 위해 +1)
        .fetch();                                                // 결과 조회
  }

  // 정렬 조건 생성 헬퍼 메서드
  private List<OrderSpecifier<?>> createOrderSpecifiers(String direction) {
    Order order = "ASC".equalsIgnoreCase(direction) ? Order.ASC : Order.DESC;

    return List.of(
        new OrderSpecifier<>(order, qComment.createdAt), // 1순위: createdAt
        new OrderSpecifier<>(order, qComment.id)        // 2순위: id (createdAt 중복 시)
    );
  }

  // 커서 조건 생성 헬퍼 메서드
  private BooleanExpression createCursorCondition(String direction, Instant after, Long cursorId) {
    // 첫 페이지 조회 시 (커서 값 없음)
    if (after == null || cursorId == null) {
      return null;
    }

    // --- '메인+보조 커서' WHERE 조건 ---
    if ("ASC".equalsIgnoreCase(direction)) {
      // 오름차순 (ASC): (createdAt > ?) OR (createdAt = ? AND id > ?)
      return qComment.createdAt.gt(after)
          .or(qComment.createdAt.eq(after).and(qComment.id.gt(cursorId)));
    } else {
      // 내림차순 (DESC): (createdAt < ?) OR (createdAt = ? AND id < ?)
      return qComment.createdAt.lt(after)
          .or(qComment.createdAt.eq(after).and(qComment.id.lt(cursorId)));
    }
  }
}