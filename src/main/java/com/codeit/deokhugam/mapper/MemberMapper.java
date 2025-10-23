package com.codeit.deokhugam.mapper;

import com.codeit.deokhugam.domain.entity.Member;
import com.codeit.deokhugam.dto.command.MemberCreateCommand;
import com.codeit.deokhugam.dto.command.MemberLoginCommand;
import com.codeit.deokhugam.dto.request.MemberCreateRequest;
import com.codeit.deokhugam.dto.request.MemberLoginRequest;
import com.codeit.deokhugam.dto.response.MemberCreatedResponse;
import com.codeit.deokhugam.dto.response.MemberLoginResponse;
import com.codeit.deokhugam.dto.result.MemberCreatedResult;
import com.codeit.deokhugam.dto.result.MemberLoginResult;
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

}
