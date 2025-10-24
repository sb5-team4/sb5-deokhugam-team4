package com.codeit.deokhugam.dto.command.member;

public record MemberCreateCommand(
    String email,
    String nickname,
    String password
) {

}
