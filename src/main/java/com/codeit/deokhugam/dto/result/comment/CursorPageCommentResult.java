package com.codeit.deokhugam.dto.result.comment;

import com.codeit.deokhugam.dto.response.comment.CommentResponse;
import java.time.Instant;
import java.util.List;

public record CursorPageCommentResult(

    //댓글 목록 조회 Service의 비즈니스 로직 결과 객체
    List<CommentResponse> comments,
    String nextCursor,
    Instant nextAfter,
    int size,
    long totalElements,
    boolean hasNext
  ) {
}
