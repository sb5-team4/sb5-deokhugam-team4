package com.codeit.deokhugam.controller;

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
import com.codeit.deokhugam.mapper.MemberMapper;
import com.codeit.deokhugam.service.impl.MemberService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
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

  @PostMapping(path = "/login")
  public ResponseEntity<MemberLoginResponse> login(
      @RequestBody @Valid MemberLoginRequest request) {
    MemberLoginCommand memberLoginCommand = memberMapper.toMemberLoginCommand(request);
    MemberLoginResult memberLoginResult = memberService.login(memberLoginCommand);
    MemberLoginResponse memberLoginResponse = memberMapper.toMemberLoginResponse(memberLoginResult);
    return ResponseEntity.status(HttpStatus.OK).body(memberLoginResponse);
  }

  //미구현, 로그인시 접근가능한지, 로그인후 요청헤더에 id값이 들어오는지 테스트용
  @PatchMapping(path = "/{userId}")
  public ResponseEntity<Void> update(
      @PathVariable("userId") long userId,
      @RequestHeader("Deokhugam-Request-User-ID") long id) {
    log.info("id  {}", userId);
    log.info("Update member with header_id  {}", id);
    return ResponseEntity.noContent().build();
  }

  //사용자 정보조회(단일)
  // 사용자 id값을 받고, id,email,nickname,생성시간 반환
  @GetMapping(path = "/{memberId}")
  public ResponseEntity<MemberFindResponse> getMember(@PathVariable Long memberId) {
    MemberFindResult result = memberService.findById(memberId);
    return ResponseEntity.status(HttpStatus.OK).body(memberMapper.toMemberFindResponse(result));
  }


}
