package com.codeit.deokhugam.dto.response.member;

import java.time.Instant;

public record MemberLoginResponse(
    Long id,
    String email,
    String nickname,
    Instant createdAt
) {

}
