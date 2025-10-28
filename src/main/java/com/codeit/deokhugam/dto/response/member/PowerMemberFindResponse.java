package com.codeit.deokhugam.dto.response.member;

import java.time.Instant;
import java.util.List;

public record PowerMemberFindResponse(
    List<PowerMemberDto> content,
    String nextCursor,
    Instant nextAfter,
    int size,
    long totalElements,
    boolean hasNext
) {

}
