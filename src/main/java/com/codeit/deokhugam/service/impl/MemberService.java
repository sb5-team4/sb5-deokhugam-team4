package com.codeit.deokhugam.service.impl;

import com.codeit.deokhugam.domain.entity.Member;
import com.codeit.deokhugam.dto.command.MemberCreateCommand;
import com.codeit.deokhugam.dto.result.MemberCreatedResult;
import com.codeit.deokhugam.mapper.MemberMapper;
import com.codeit.deokhugam.repository.MemberRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class MemberService {

  private final MemberMapper memberMapper;
  private final MemberRepository memberRepository;

  @Transactional
  public MemberCreatedResult create(MemberCreateCommand memberCreateCommand) {
    if (memberRepository.existsByEmail(memberCreateCommand.email())) {
      throw new RuntimeException("Email already exists");
    }
    if (memberRepository.existsByNickname(memberCreateCommand.nickname())) {
      throw new RuntimeException("Nickname already exists");
    }
    Member createMember = memberMapper.toMember(memberCreateCommand);
    Member returnMember = memberRepository.save(createMember);
    return memberMapper.toMemberCreatedResult(returnMember);


  }
}
