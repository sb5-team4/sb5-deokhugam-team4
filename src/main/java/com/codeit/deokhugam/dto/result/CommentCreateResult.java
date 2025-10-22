package com.codeit.deokhugam.dto.result;

import java.time.Instant;

public record CommentCreateResult(
    Long id,
    Long reviewId,
    Long memberId,
    String nickname,
    String content,
    Instant createdAt,
    Instant updatedAt
) {

}
