package com.codeit.deokhugam.dto.response;

import java.time.Instant;

public record CommentResponse(
    Long id,
    Long reviewId,
    Long memberId,
    String nickname,
    String content,
    Instant createdAt,
    Instant updatedAt
) {

}
