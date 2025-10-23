package com.codeit.deokhugam.service.impl;

import com.codeit.deokhugam.domain.entity.Member;
import com.codeit.deokhugam.dto.command.MemberCreateCommand;
import com.codeit.deokhugam.dto.command.MemberLoginCommand;
import com.codeit.deokhugam.dto.result.MemberCreatedResult;
import com.codeit.deokhugam.dto.result.MemberLoginResult;
import com.codeit.deokhugam.mapper.MemberMapper;
import com.codeit.deokhugam.repository.MemberRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class MemberService {

  private final MemberMapper memberMapper;
  private final MemberRepository memberRepository;
  private final PasswordEncoder passwordEncoder;


  @Transactional
  public MemberCreatedResult create(MemberCreateCommand memberCreateCommand) {
    if (memberRepository.existsByEmail(memberCreateCommand.email())) {
      throw new RuntimeException("Email already exists");
    }
    if (memberRepository.existsByNickname(memberCreateCommand.nickname())) {
      throw new RuntimeException("Nickname already exists");
    }
    Member createMember = memberMapper.toMember(memberCreateCommand);
    //record는 setter불가라 엔티티 변환후 패스워드 암호화
    String encodedPw = passwordEncoder.encode(createMember.getPassword());
    createMember.setPassword(encodedPw);
    Member returnMember = memberRepository.save(createMember);
    return memberMapper.toMemberCreatedResult(returnMember);
  }

  public MemberLoginResult login(MemberLoginCommand memberLoginCommand) {
    Member member;
    if (memberRepository.existsByEmail(memberLoginCommand.email())) {
      member = memberRepository.findByEmail(memberLoginCommand.email());

      if (!passwordEncoder.matches(memberLoginCommand.password(), member.getPassword())) {
        throw new RuntimeException("Wrong password");
      }
    } else {
      throw new RuntimeException("존재하지않는 이메일입니다.");
    }
    return memberMapper.toMemberLoginResult(member);
  }
}
