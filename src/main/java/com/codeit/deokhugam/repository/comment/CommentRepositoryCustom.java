package com.codeit.deokhugam.repository.comment;

import com.codeit.deokhugam.domain.entity.Comment;
import java.time.Instant;
import java.util.List;

public interface CommentRepositoryCustom {

  /**
   * 특정 리뷰의 댓글 목록을 커서 기반 페이지네이션으로 조회
   *
   * @param reviewId  댓글을 조회할 리뷰 ID
   * @param direction 정렬 방향 ("ASC" 또는 "DESC")
   * @param after     이전 페이지의 마지막 댓글 createdAt 값 (첫 페이지는 null)
   * @param limit     페이지 크기
   * @return 댓글 목록 Slice (또는 직접 만든 CursorPageResponse)
   */
  List<Comment> findByReviewId(Long reviewId, String direction, Instant after, Long cursorId, int limit);

  }
