package com.codeit.deokhugam.controller;

import com.codeit.deokhugam.dto.command.MemberCreateCommand;
import com.codeit.deokhugam.dto.command.MemberLoginCommand;
import com.codeit.deokhugam.dto.request.MemberCreateRequest;
import com.codeit.deokhugam.dto.request.MemberLoginRequest;
import com.codeit.deokhugam.dto.response.MemberCreatedResponse;
import com.codeit.deokhugam.dto.response.MemberLoginResponse;
import com.codeit.deokhugam.dto.result.MemberCreatedResult;
import com.codeit.deokhugam.dto.result.MemberLoginResult;
import com.codeit.deokhugam.mapper.MemberMapper;
import com.codeit.deokhugam.service.impl.MemberService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/users")
public class MemberController {

  private final MemberService memberService;
  private final MemberMapper memberMapper;


  @PostMapping
  public ResponseEntity<MemberCreatedResponse> create(
      @Valid @RequestBody MemberCreateRequest request
  ) {
    MemberCreateCommand memberCreateCommand = memberMapper.toMemberCreateCommand(request);
    MemberCreatedResult created = memberService.create(memberCreateCommand);
    MemberCreatedResponse memberCreatedResponse = memberMapper.toMemberCreatedResponse(created);
    return ResponseEntity.status(HttpStatus.CREATED).body(memberCreatedResponse);
  }

  @PostMapping(path = "login")
  public ResponseEntity<MemberLoginResponse> login(
      @RequestBody @Valid MemberLoginRequest request) {
    MemberLoginCommand memberLoginCommand = memberMapper.toMemberLoginCommand(request);
    MemberLoginResult memberLoginResult = memberService.login(memberLoginCommand);
    MemberLoginResponse memberLoginResponse = memberMapper.toMemberLoginResponse(memberLoginResult);
    return ResponseEntity.status(HttpStatus.OK).body(memberLoginResponse);
  }

  //미구현, 로그인시 접근가능한지, 로그인후 요청헤더에 id값이 들어오는지 테스트용
  @PatchMapping(path = "{userId}")
  public ResponseEntity<Void> update(
      @PathVariable("userId") long userId,
      @RequestHeader("Deokhugam-Request-User-ID") long id) {
    log.info("id  {}", userId);
    log.info("Update member with header_id  {}", id);
    return ResponseEntity.noContent().build();
  }


}
