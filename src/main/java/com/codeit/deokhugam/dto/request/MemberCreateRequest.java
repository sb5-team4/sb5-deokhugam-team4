package com.codeit.deokhugam.dto.request;

public record MemberCreateRequest(
    String email,
    String nickname,
    String password
) {

}
