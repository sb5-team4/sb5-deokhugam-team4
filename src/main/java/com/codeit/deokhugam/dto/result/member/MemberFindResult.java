package com.codeit.deokhugam.dto.result.member;

import java.time.Instant;

public record MemberFindResult(
    String id,
    String email,
    String nickname,
    Instant createdAt
) {

}
