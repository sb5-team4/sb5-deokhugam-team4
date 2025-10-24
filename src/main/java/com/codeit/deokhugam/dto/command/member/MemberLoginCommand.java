package com.codeit.deokhugam.dto.command.member;

public record MemberLoginCommand(
    String email,
    String password
) {

}
