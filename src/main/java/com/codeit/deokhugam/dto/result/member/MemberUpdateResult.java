package com.codeit.deokhugam.dto.result.member;

import java.time.Instant;

public record MemberUpdateResult(
    Long id,
    String email,
    String nickname,
    Instant createdAt
) {

}
