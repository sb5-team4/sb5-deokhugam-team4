package com.codeit.deokhugam.dto.result;

import java.time.Instant;

public record MemberLoginResult(
    Long id,
    String email,
    String nickname,
    Instant createdAt
) {

}
