package com.codeit.deokhugam.dto.response.member;

import java.time.Instant;

public record MemberFindResponse(
    String id,
    String email,
    String nickname,
    Instant createdAt
) {

}
