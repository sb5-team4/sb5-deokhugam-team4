package com.codeit.deokhugam.batch.processor;

import com.codeit.deokhugam.batch.dto.PowerMemberScoreDto;
import com.codeit.deokhugam.domain.entity.Member;
import com.codeit.deokhugam.domain.entity.PowerMember;
import com.codeit.deokhugam.repository.MemberRepository;
import java.math.BigDecimal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemProcessor;

@Slf4j
public class PowerMemberProcessor implements ItemProcessor<PowerMemberScoreDto, PowerMember> {

  private final MemberRepository memberRepository;

  public PowerMemberProcessor(MemberRepository memberRepository) {
    this.memberRepository = memberRepository;
  }

  @Override
  public PowerMember process(PowerMemberScoreDto dto) {
    Member member = memberRepository.findById(dto.getMemberId())
        .orElseThrow(() -> new RuntimeException("Member not found: " + dto.getMemberId()));

    BigDecimal score = BigDecimal.valueOf(
        dto.getReviewScoreSum() * 0.5 +
            dto.getLikeCount() * 0.2 +
            dto.getCommentCount() * 0.3
    );

    log.info("Processing DTO: {}, Result entity: {}", dto, score);

    return PowerMember.builder()
        .member(member)
        .score(score)
        .reviewScoreSum(BigDecimal.valueOf(dto.getReviewScoreSum()))
        .likeCount(dto.getLikeCount())
        .commentCount(dto.getCommentCount())
        .period(dto.getPeriod())  // DTO에서 가져옴
        .rank(0L)
        .build();
  }
}
