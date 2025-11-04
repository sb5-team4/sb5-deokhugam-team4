package com.codeit.deokhugam.dto.response.comment;

import java.time.Instant;

public record CommentResponse(
    Long id,
    Long reviewId,
    Long userId,
    String nickname,
    String content,
    Instant createdAt,
    Instant updatedAt
) {

}
