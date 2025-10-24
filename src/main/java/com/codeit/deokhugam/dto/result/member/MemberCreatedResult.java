package com.codeit.deokhugam.dto.result.member;

import java.time.Instant;

public record MemberCreatedResult(
    Long id,
    String nickname,
    String email,
    Instant createdAt
) {

}
