package com.codeit.deokhugam.controller;

import com.codeit.deokhugam.domain.enums.Period;
import com.codeit.deokhugam.dto.command.member.MemberCreateCommand;
import com.codeit.deokhugam.dto.command.member.MemberLoginCommand;
import com.codeit.deokhugam.dto.request.member.MemberCreateRequest;
import com.codeit.deokhugam.dto.request.member.MemberLoginRequest;
import com.codeit.deokhugam.dto.request.member.MemberUpdateRequest;
import com.codeit.deokhugam.dto.response.member.MemberCreatedResponse;
import com.codeit.deokhugam.dto.response.member.MemberFindResponse;
import com.codeit.deokhugam.dto.response.member.MemberLoginResponse;
import com.codeit.deokhugam.dto.response.member.MemberUpdateResponse;
import com.codeit.deokhugam.dto.response.member.PowerMemberFindResponse;
import com.codeit.deokhugam.dto.result.member.MemberCreatedResult;
import com.codeit.deokhugam.dto.result.member.MemberFindResult;
import com.codeit.deokhugam.dto.result.member.MemberLoginResult;
import com.codeit.deokhugam.dto.result.member.MemberUpdateResult;
import com.codeit.deokhugam.dto.result.member.PowerMemberFindResult;
import com.codeit.deokhugam.mapper.MemberMapper;
import com.codeit.deokhugam.service.impl.MemberService;
import com.querydsl.core.types.Order;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import java.time.Instant;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
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
      @RequestBody @Valid MemberLoginRequest request
      /*HttpServletRequest servletReqest*/) {
    MemberLoginCommand memberLoginCommand = memberMapper.toMemberLoginCommand(request);
    MemberLoginResult memberLoginResult = memberService.login(memberLoginCommand);

//    servletReqest.getSession().invalidate(); // 이전 세션 무효화
//    HttpSession session = servletReqest.getSession(true); // 새 세션 생성
//    session.setAttribute("memberId", memberLoginResult.id());

    MemberLoginResponse memberLoginResponse = memberMapper.toMemberLoginResponse(memberLoginResult);
    return ResponseEntity.status(HttpStatus.OK).body(memberLoginResponse);
  }

  //사용자 닉네임 업데이트 (닉네임만가능)
  //사용자 아이디값 받음
  // id, email, 수정된 nickname, 생성날짜 반환
  @PatchMapping(path = "/{memberId}")
  public ResponseEntity<MemberUpdateResponse> update(
      @PathVariable("memberId") Long memberId,
      @RequestBody @Valid MemberUpdateRequest request) {
    String nickname = request.nickname();
    MemberUpdateResult result = memberService.update(memberId,
        nickname);
    return ResponseEntity.status(HttpStatus.OK).body(memberMapper.toMemberUpdateResponse(result));

  }

  //사용자 정보조회(단일)
  // 사용자 id값을 받고, id,email,nickname,생성시간 반환
  @GetMapping(path = "/{memberId}")
  public ResponseEntity<MemberFindResponse> getMember(@PathVariable Long memberId) {
    MemberFindResult result = memberService.findById(memberId);
    return ResponseEntity.status(HttpStatus.OK).body(memberMapper.toMemberFindResponse(result));
  }

  //멤버 논리삭제
  // 사용자 id값을 경로로 받고, 204 no content 상태코드 반환
  @DeleteMapping(path = "/{memberId}")
  public ResponseEntity<Void> softDeleteMember(@PathVariable Long memberId,
      @RequestHeader("Deokhugam-Request-User-ID") Long headerId
  ) {
    memberService.softDelete(memberId, headerId);
    return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
  }

  //파워유저 목록조회
  // 커서기반 페이지응답
  @GetMapping(path = "/power")
  public ResponseEntity<PowerMemberFindResponse> findPowerMember(
      @RequestParam(defaultValue = "DAILY") Period period,
      @RequestParam(defaultValue = "ASC") Order direction,
      @RequestParam(required = false) @Min(0) Long cursor,  //만약값이있다면 양수 / null허용이라 Long타입
      @RequestParam(required = false) Instant after,
      @RequestParam(defaultValue = "50") int limit
  ) {
    PowerMemberFindResult result = memberService.findPowerMember(
        memberMapper.toPowerMemberFindCommand(period, direction, cursor, after, limit));
    return ResponseEntity.status(HttpStatus.OK)
        .body(memberMapper.toPowerMemberFindResponse(result));


  }

  @DeleteMapping(path = "/{memberId}/hard")
  public ResponseEntity<Void> softDeleteMemberHard(
      @PathVariable Long memberId
  ) {
    memberService.hardDelete(memberId);
    return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
  }

  //파워멤버 랭킹 배치시스템 테스트용
  //요청,응답 데이터없음, 상태코드반환
  @PostMapping("/batch")
  public ResponseEntity<String> runBatch() throws Exception {
    memberService.runPowerMemberJob();
    return ResponseEntity.ok("🚀 PowerMember 배치 실행 요청 완료!");
  }


}
