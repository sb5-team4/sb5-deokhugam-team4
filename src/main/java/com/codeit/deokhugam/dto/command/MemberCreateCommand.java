package com.codeit.deokhugam.dto.command;

public record MemberCreateCommand(
    String email,
    String nickname,
    String password
) {

}
