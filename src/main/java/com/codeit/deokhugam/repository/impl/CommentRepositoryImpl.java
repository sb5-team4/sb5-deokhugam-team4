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
  public List<Comment> findByCommentReviewIdWithCursor(Long reviewId, String direction, Instant after, int limitComment) {

    OrderSpecifier<?> orderSpecifier = createOrderSpecifier(direction); // 정렬 조건 생성
    BooleanExpression cursorCondition = createCursorCondition(direction, after); // 커서 조건 생성

    return queryFactory
        .selectFrom(qComment)                // SELECT * FROM comment
        .where(
            qComment.review.id.eq(reviewId), // WHERE review_id = ?
            cursorCondition                  // AND (created_at < ? OR created_at > ?)
        )
        .orderBy(orderSpecifier)             // ORDER BY created_at [ASC|DESC]
        .limit(limitComment + 1)                    // LIMIT ? (다음 페이지 확인 위해 +1)
        .fetch();                            // 결과 조회
  }

  // 정렬 조건 생성 헬퍼 메서드
  private OrderSpecifier<?> createOrderSpecifier(String direction) {
    Order order = "ASC".equalsIgnoreCase(direction) ? Order.ASC : Order.DESC;
    // qComment.createdAt 접근 (BaseEntity 상속 필드)
    return new OrderSpecifier<>(order, qComment.createdAt);
  }

  // 커서 조건 생성 헬퍼 메서드
  private BooleanExpression createCursorCondition(String direction, Instant after) {
    if (after == null) {
      return null; // 첫 페이지는 커서 조건 없음
    }

    // qComment.createdAt 접근 (BaseEntity 상속 필드)
    if ("ASC".equalsIgnoreCase(direction)) {
      return qComment.createdAt.gt(after); // WHERE created_at > ?
    } else {
      return qComment.createdAt.lt(after); // WHERE created_at < ?
    }
  }
}