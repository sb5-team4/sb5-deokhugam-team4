package com.codeit.deokhugam.dto.response;

import java.time.Instant;

public record MemberLoginResponse(
    Long id,
    String email,
    String nickname,
    Instant createdAt
) {

}
