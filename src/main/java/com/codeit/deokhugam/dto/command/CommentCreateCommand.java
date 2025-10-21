package com.codeit.deokhugam.dto.command;

public record CommentCreateCommand(Long reviewId, Long memberId, String content) {

}
