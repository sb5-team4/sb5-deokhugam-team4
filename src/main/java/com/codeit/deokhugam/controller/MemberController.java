package com.codeit.deokhugam.controller;

import com.codeit.deokhugam.dto.command.MemberCreateCommand;
import com.codeit.deokhugam.dto.request.MemberCreateRequest;
import com.codeit.deokhugam.dto.response.MemberCreatedResponse;
import com.codeit.deokhugam.dto.result.MemberCreatedResult;
import com.codeit.deokhugam.mapper.MemberMapper;
import com.codeit.deokhugam.service.impl.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class MemberController {

  private final MemberService memberService;
  private final MemberMapper memberMapper;


  @PostMapping
  public ResponseEntity<MemberCreatedResponse> create(
      @RequestBody MemberCreateRequest user
  ) {
    MemberCreateCommand memberCreateCommand = memberMapper.toMemberCreateCommand(user);
    MemberCreatedResult created = memberService.create(memberCreateCommand);
    MemberCreatedResponse memberCreatedResponse = memberMapper.toMemberCreatedResponse(created);
    return ResponseEntity.ok().body(memberCreatedResponse);
  }
}
