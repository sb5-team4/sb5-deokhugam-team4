package com.codeit.deokhugam.dto.response.comment;


import java.time.Instant;
import java.util.List;

/* 댓글 목록 조회  (GET /api/comments)
   커서 기반 페이지네이션 응답
 */
public record CursorPageCommentResponse(
    // 실제 댓글 리스트 호출
    List<CommentResponse> comments,

    // 페이지네이션 정보
    String nextCursor,
    Instant nextAfter,
    int size,
    long totalElements,
    boolean hasNext
) {

}
