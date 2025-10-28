package com.codeit.deokhugam.repository.impl;

import com.codeit.deokhugam.domain.entity.QMember;
import com.codeit.deokhugam.domain.entity.QPowerMember;
import com.codeit.deokhugam.dto.command.member.PowerMemberFindCommand;
import com.codeit.deokhugam.dto.response.member.PowerMemberDto;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class MemberQueryRepository {

  private final JPAQueryFactory queryFactory;
  private static final QMember m = QMember.member;
  private static final QPowerMember p = QPowerMember.powerMember;

  public Slice<PowerMemberDto> findPowerMembers(PowerMemberFindCommand command) {
    //페이징 처리 기본 코드
    long rank =
        (command.cursor() != null && command.cursor() > 0L) ? command.cursor() : 0L; //null, 음수방지
    int size = command.limit();
    //after period direction
    // where 절
    BooleanBuilder builder = new BooleanBuilder();
    //파워멤버는 soft delete 없음

    //동적 쿼리부

    //after가있다면 after시간 이후 쿼리 검색
//    if (command.after() != null) {  //**after는 보험용 필터인데 랭킹테이블은 생성시간이 같을 확률이 높아 안쓰는게 나을듯?
//      builder.and(p.createdAt.after(command.after()));
//    }
    if (command.direction() == Order.ASC) {
      builder.and(p.rank.gt(rank));
    } else { // DESC
      builder.and(p.rank.lt(rank));
    }
    // period 조건 추가
    builder.and(p.period.eq(command.period().name()));

    //정렬 방향 설정
    Order order = command.direction(); // 기본값 ASC
    //정렬 기준
    OrderSpecifier<?> orderById = new OrderSpecifier<>(order, p.id);

    //컨텐츠 조회
    // slice 컨텐츠 조회시 limit+1 -> hasNext를 표현하기 위해
    List<PowerMemberDto> rowsPlusOne = queryFactory
        .selectDistinct(p)
        .select(Projections.constructor(
            PowerMemberDto.class,
            p.id,
            m.nickname,
            p.period,
            p.createdAt,
            p.rank.longValue(),           // Long → long
            p.score.doubleValue(),        // BigDecimal → double
            p.reviewScoreSum.doubleValue(), // BigDecimal → double
            p.likeCount.longValue(),      // Long → long
            p.commentCount.longValue()    // Long → long
        ))
        .from(p)
        .join(p.member, m)
        .where(builder)
        .orderBy(orderById)
        //.offset((long) page * size) //offset: 앞에서 몇개를 건너뛸까? // 대용량데이터에 좋지않음
        .limit(size + 1L) //hasNext 계산용 +1 / 반환할떄 size 만큼 잘라서 반환
        .fetch(); // n+1방지

    //hasNext 계산하기
    boolean hasNext = rowsPlusOne.size() > size;
    List<PowerMemberDto> contents = hasNext ? rowsPlusOne.subList(0, size) : rowsPlusOne;
    Pageable pageable = PageRequest.of(0, size, //slice기반이라 페이지번호는 의미없어서 0
        (order == Order.ASC)
            ? Sort.by(Sort.Direction.ASC, "id")
            : Sort.by(Sort.Direction.DESC, "id"));

    return new SliceImpl<>(contents, pageable, hasNext);

  }
}
