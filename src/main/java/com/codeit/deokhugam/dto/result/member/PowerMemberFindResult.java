package com.codeit.deokhugam.dto.result.member;

import com.codeit.deokhugam.dto.response.member.PowerMemberDto;
import java.time.Instant;
import java.util.List;

public record PowerMemberFindResult(
    List<PowerMemberDto> content,
    Long nextCursor,
    Instant nextAfter,
    Integer size,
    long totalElements,
    boolean hasNext
) {

}
