package com.codeit.deokhugam.dto.command.comment;

public record CommentCreateCommand(Long reviewId, Long memberId, String content) {

}
