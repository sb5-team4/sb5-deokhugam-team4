package com.codeit.deokhugam.dto.command.comment;

import java.time.Instant;

public record CursorPageCommentCommand(
    Long reviewId,
    String direction,
    Instant after,
    Long cursorId,
    int commentLimit
) {
}
