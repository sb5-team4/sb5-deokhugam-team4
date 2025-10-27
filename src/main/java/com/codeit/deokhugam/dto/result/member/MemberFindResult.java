package com.codeit.deokhugam.dto.result.member;

import java.time.Instant;

public record MemberFindResult(
    Long id,
    String email,
    String nickname,
    Instant createdAt
) {

}
