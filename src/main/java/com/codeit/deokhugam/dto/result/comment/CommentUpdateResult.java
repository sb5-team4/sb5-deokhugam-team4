package com.codeit.deokhugam.dto.result.comment;

import java.time.Instant;

public record CommentUpdateResult(
    Long id,
    Long reviewId,
    Long memberId,
    String nickname,
    String content,
    Instant createdAt,
    Instant updatedAt
) {

}
