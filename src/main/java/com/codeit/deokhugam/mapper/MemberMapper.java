package com.codeit.deokhugam.mapper;

import com.codeit.deokhugam.domain.entity.Member;
import com.codeit.deokhugam.dto.command.MemberCreateCommand;
import com.codeit.deokhugam.dto.request.MemberCreateRequest;
import com.codeit.deokhugam.dto.response.MemberCreatedResponse;
import com.codeit.deokhugam.dto.result.MemberCreatedResult;
import org.springframework.stereotype.Component;

@Component
public class MemberMapper {

  //푸쉬테스트
  //  @Mapping(target = "id", ignore = true)
//  @Mapping(target = "createdAt", ignore = true)
//  @Mapping(target = "updatedAt", ignore = true)
  public MemberCreateCommand toMemberCreateCommand(
      MemberCreateRequest memberCreateRequest) {
    return new MemberCreateCommand(
        memberCreateRequest.email(),
        memberCreateRequest.nickname(),
        memberCreateRequest.password()
    );

  }

  public Member toMember(MemberCreateCommand memberCreateCommand) {
    return new Member(
        memberCreateCommand.email(),
        memberCreateCommand.nickname(),
        memberCreateCommand.password(),
        false
    );
  }

  public MemberCreatedResult toMemberCreatedResult(Member member) {
    return new MemberCreatedResult(
        member.getId(),
        member.getNickname(),
        member.getEmail(),
        member.getCreatedAt()
    );
  }

  public MemberCreatedResponse toMemberCreatedResponse(
      MemberCreatedResult memberCreatedResult) {
    return new MemberCreatedResponse(
        memberCreatedResult.id(),
        memberCreatedResult.nickname(),
        memberCreatedResult.email(),
        memberCreatedResult.createdAt()
    );
  }

}
