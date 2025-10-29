package com.codeit.deokhugam.dto.command.member;

import com.codeit.deokhugam.domain.enums.Period;
import com.querydsl.core.types.Order;
import java.time.Instant;

public record PowerMemberFindCommand(
    Period period,
    Order direction,
    Long cursor,
    Instant after,
    int limit

) {

}
