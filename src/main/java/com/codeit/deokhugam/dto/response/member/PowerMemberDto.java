package com.codeit.deokhugam.dto.response.member;

import java.time.Instant;

public record PowerMemberDto(
    Long memberId,
    String nickname,
    String period,
    Instant createdAt,
    long rank,
    double score,
    double reviewScoreSum,
    long likeCount,
    long commentCount
) {

}
