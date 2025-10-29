package com.codeit.deokhugam.service.impl;

import com.codeit.deokhugam.common.exception.handler.CustomException;
import com.codeit.deokhugam.common.exception.handler.ErrorCode;
import com.codeit.deokhugam.domain.entity.Member;
import com.codeit.deokhugam.dto.command.member.MemberCreateCommand;
import com.codeit.deokhugam.dto.command.member.MemberLoginCommand;
import com.codeit.deokhugam.dto.command.member.PowerMemberFindCommand;
import com.codeit.deokhugam.dto.response.member.PowerMemberDto;
import com.codeit.deokhugam.dto.result.member.MemberCreatedResult;
import com.codeit.deokhugam.dto.result.member.MemberFindResult;
import com.codeit.deokhugam.dto.result.member.MemberLoginResult;
import com.codeit.deokhugam.dto.result.member.MemberUpdateResult;
import com.codeit.deokhugam.dto.result.member.PowerMemberFindResult;
import com.codeit.deokhugam.mapper.MemberMapper;
import com.codeit.deokhugam.repository.MemberRepository;
import com.codeit.deokhugam.repository.impl.MemberQueryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Slice;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class MemberService {

  private final MemberMapper memberMapper;
  private final MemberRepository memberRepository;
  private final MemberQueryRepository memberQueryRepository;
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
    createMember.encodePassword(encodedPw);
    Member returnMember = memberRepository.save(createMember);
    return memberMapper.toMemberCreatedResult(returnMember);
  }

  @Transactional(readOnly = true)
  public MemberLoginResult login(MemberLoginCommand memberLoginCommand) {
    Member member;
    if (memberRepository.existsByEmail(memberLoginCommand.email())) {
      member = memberRepository.findByEmail(memberLoginCommand.email())
          .orElseThrow(() -> new RuntimeException("존재하지 않는 이메일입니다."));

      if (!passwordEncoder.matches(memberLoginCommand.password(), member.getPassword())) {
        throw new RuntimeException("Wrong password");
      }
    } else {
      throw new RuntimeException("존재하지않는 이메일입니다.");
    }
    return memberMapper.toMemberLoginResult(member);
  }

  @Transactional(readOnly = true)
  public MemberFindResult findById(Long id) {
    Member member = memberRepository.findById(id)
        .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
    return memberMapper.toMemberFindResult(member);
  }

  @Transactional
  public MemberUpdateResult update(Long memberId, String nickname) {
    Member member = memberRepository.findById(memberId)
        .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
    member.updateNickname(nickname);
    memberRepository.save(member);
    return memberMapper.toMemberUpdateResult(member);

  }

  @Transactional
  public void softDelete(Long memberId, Long headerId
  ) {
    if (!memberId.equals(headerId)) {  //로그인후 요청에 넣는 로그인유저id값과 요청 유저id값이 같은지
      throw new CustomException(ErrorCode.USER_NOT_AUTHORIZED);
    }
    Member deleteMember = memberRepository.findById(memberId)
        .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
    deleteMember.isSoftDeleted();

    //소프트딜리트시 연관데이터 소프트딜리트?

  }

  @Transactional(readOnly = true)
  public PowerMemberFindResult findPowerMember(PowerMemberFindCommand command) {
    Slice<PowerMemberDto> slice = memberQueryRepository.findPowerMembers(command);
    PowerMemberFindResult result = new PowerMemberFindResult(
        slice.getContent(),
        slice.hasNext() ? slice.getContent().get(slice.getSize() - 1).rank() : null,
        slice.hasNext() && !slice.isEmpty() ? slice.getContent().get(slice.getSize() - 1)
            .createdAt() : null,
        slice.getSize(),
        slice.getNumberOfElements(),
        slice.hasNext()
    );

    return result;
  }


}
