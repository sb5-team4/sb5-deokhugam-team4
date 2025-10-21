package com.codeit.deokhugam.dto.response;

import java.time.Instant;

public record MemberCreatedResponse(
    Long id,
    String email,
    String nickname,
    Instant createdAt
) {

}
