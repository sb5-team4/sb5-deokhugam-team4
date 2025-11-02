package com.codeit.deokhugam.dto.command.comment;

public record CommentUpdateCommand(
    Long commentId,
    Long requestMemberId,
    String content
) {

}
