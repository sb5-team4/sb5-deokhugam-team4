package com.codeit.deokhugam.mapper;

import com.codeit.deokhugam.domain.entity.Member;
import com.codeit.deokhugam.dto.command.member.MemberCreateCommand;
import com.codeit.deokhugam.dto.command.member.MemberLoginCommand;
import com.codeit.deokhugam.dto.request.member.MemberCreateRequest;
import com.codeit.deokhugam.dto.request.member.MemberLoginRequest;
import com.codeit.deokhugam.dto.response.member.MemberCreatedResponse;
import com.codeit.deokhugam.dto.response.member.MemberFindResponse;
import com.codeit.deokhugam.dto.response.member.MemberLoginResponse;
import com.codeit.deokhugam.dto.result.member.MemberCreatedResult;
import com.codeit.deokhugam.dto.result.member.MemberFindResult;
import com.codeit.deokhugam.dto.result.member.MemberLoginResult;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public abstract class MemberMapper {

  //  @Mapping(target = "id", ignore = true)
//  @Mapping(target = "createdAt", ignore = true)
//  @Mapping(target = "updatedAt", ignore = true)

  //create
  public abstract MemberCreateCommand toMemberCreateCommand(
      MemberCreateRequest memberCreateRequest);

  @Mapping(target = "deleted", constant = "false")
  public abstract Member toMember(MemberCreateCommand memberCreateCommand);

  public abstract MemberCreatedResult toMemberCreatedResult(Member member);

  public abstract MemberCreatedResponse toMemberCreatedResponse(
      MemberCreatedResult memberCreatedResult);

  //login
  public abstract MemberLoginCommand toMemberLoginCommand(
      MemberLoginRequest memberLoginRequest);

  public abstract MemberLoginResult toMemberLoginResult(Member member);

  public abstract MemberLoginResponse toMemberLoginResponse(
      MemberLoginResult memberLogindResult);

  //Find(단일)
  public abstract MemberFindResult toMemberFindResult(Member member);

  public abstract MemberFindResponse toMemberFindResponse(MemberFindResult memberFindResult);

}
