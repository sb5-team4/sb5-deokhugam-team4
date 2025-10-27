package com.codeit.deokhugam.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.codeit.deokhugam.common.exception.handler.CustomException;
import com.codeit.deokhugam.common.exception.handler.ErrorCode;
import com.codeit.deokhugam.common.exception.handler.GlobalExceptionHandler;
import com.codeit.deokhugam.domain.entity.Member;
import com.codeit.deokhugam.dto.command.member.MemberCreateCommand;
import com.codeit.deokhugam.dto.command.member.MemberLoginCommand;
import com.codeit.deokhugam.dto.request.member.MemberCreateRequest;
import com.codeit.deokhugam.dto.request.member.MemberLoginRequest;
import com.codeit.deokhugam.dto.response.member.MemberCreatedResponse;
import com.codeit.deokhugam.dto.response.member.MemberFindResponse;
import com.codeit.deokhugam.dto.response.member.MemberLoginResponse;
import com.codeit.deokhugam.dto.response.member.MemberUpdateResponse;
import com.codeit.deokhugam.dto.result.member.MemberCreatedResult;
import com.codeit.deokhugam.dto.result.member.MemberFindResult;
import com.codeit.deokhugam.dto.result.member.MemberLoginResult;
import com.codeit.deokhugam.dto.result.member.MemberUpdateResult;
import com.codeit.deokhugam.mapper.MemberMapper;
import com.codeit.deokhugam.service.impl.MemberService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;


@WebMvcTest(MemberController.class)
@Import(GlobalExceptionHandler.class)
public class MemberControllerTest {

  @Autowired
  MockMvc mockMvc; // http 요청과 응답을 처리해줄 mock 객체

  @MockitoBean
  MemberService memberService;

  @MockitoBean
  MemberMapper memberMapper;
  @MockitoBean
  JpaMetamodelMappingContext jpaMetamodelMappingContext;

  ObjectMapper objectMapper;


  @BeforeEach
  void setUp() {
    objectMapper = new ObjectMapper(); // 여기서 직접 생성

  }

  @Test
  @DisplayName("멤버 생성 테스트")
  void createMemberSuccess() throws Exception {
    // 요청 DTO
    MemberCreateRequest request = new MemberCreateRequest("test@test.com", "nickname", "a1234567");

    // 모킹할 커맨드와 결과
    MemberCreateCommand command = new MemberCreateCommand("test@test.com", "nickname", "a1234567");
    MemberCreatedResult result = new MemberCreatedResult(1L, "nickname", "test@test.com", null);
    MemberCreatedResponse response = new MemberCreatedResponse(1L, "test@test.com", "nickname",
        null);
    Member member = new Member("test@test.com", "nickname", "a1234567", false);

    // Mapper & Service 모킹
    given(memberMapper.toMemberCreateCommand(request)).willReturn(command);
    given(memberService.create(command)).willReturn(result);
    given(memberMapper.toMember(command)).willReturn(member);
    given(memberMapper.toMemberCreatedResult(member)).willReturn(result);
    given(memberMapper.toMemberCreatedResponse(result)).willReturn(response);

    mockMvc.perform(post("/api/users")
            .contentType(MediaType.APPLICATION_JSON)
            .content("""
                {
                    "email": "test@test.com",
                    "nickname": "nickname",
                    "password": "a1234567"
                }
                """))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.email").value("test@test.com"))
        .andExpect(jsonPath("$.nickname").value("nickname"));

  }

  @Test
  @DisplayName("사용자 생성 실패 테스트 - 유효하지 않은 요청")
  void createMember_Failure_InvalidRequest() throws Exception {

    // Given
    MemberCreateRequest invalidRequest = new MemberCreateRequest(
        "t",        // 이메일 형식 위반
        "nickname", // 닉네임
        "12345678"  // 비밀번호 정책 위반
    );

    String requestBody = new ObjectMapper().writeValueAsString(invalidRequest);

    // When & Then
    mockMvc.perform(post("/api/users")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(invalidRequest)))
        .andExpect(status().isBadRequest())   // 400 Bad Request 기대
        .andExpect(jsonPath("$.errorMessage").exists()); // GlobalExceptionHandler에서 메시지 반환 시
  }

  @Test
  @DisplayName("멤버 로그인 테스트")
  void login() throws Exception {
// 요청 DTO
    MemberLoginRequest request = new MemberLoginRequest("test@test.com", "a1234567");

    // 모킹할 커맨드와 결과
    MemberLoginCommand command = new MemberLoginCommand("test@test.com", "a1234567");
    MemberLoginResult result = new MemberLoginResult(1L, "nickname", "test@test.com", null);
    MemberLoginResponse response = new MemberLoginResponse(1L, "test@test.com", "nickname",
        null);
    Member member = new Member("test@test.com", "nickname", "a1234567", false);

    // Mapper & Service 모킹
    given(memberMapper.toMemberLoginCommand(request)).willReturn(command);
    given(memberMapper.toMemberLoginResult(member)).willReturn(result);
    given(memberMapper.toMemberLoginResponse(result)).willReturn(response);
    given(memberService.login(command)).willReturn(result);

    mockMvc.perform(post("/api/users/login")
            .contentType(MediaType.APPLICATION_JSON)
            .content("""
                {
                    "email": "test@test.com",
                    "password": "a1234567"
                }
                """))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.email").value("test@test.com"))
        .andExpect(jsonPath("$.nickname").value("nickname"));
  }

  @Test
    // 테스트실패
  void loginMemberFail() throws Exception { //이메일 or 비번 안맞음
    // given
    MemberLoginRequest request = new MemberLoginRequest("wrong@email.com", "password");

    given(memberService.login(any()))
        .willThrow(new CustomException(ErrorCode.INVALID_USER_CREDENTIALS));

    // when & then
    mockMvc.perform(post("/api/users/login")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isUnauthorized()); // ✅ 401 기대
  }

  @Test
  @DisplayName("사용자 단일 조회")
  void findMemberById() throws Exception {
    Long memberId = 1L;
    Member member = new Member("test@test.com", "nickname", "a1234567", false);
    MemberFindResult result = new MemberFindResult(1L, "test@test.com", "nickname", null);
    MemberFindResponse response = new MemberFindResponse(1L, "test@test.com", "nickname", null);

    given(memberService.findById(memberId)).willReturn(result);
    given(memberMapper.toMemberFindResult(member)).willReturn(result);
    given(memberMapper.toMemberFindResponse(result)).willReturn(response);

    // when & then
    mockMvc.perform(get("/api/users/{memberId}", memberId)
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(1L))
        .andExpect(jsonPath("$.email").value("test@test.com"))
        .andExpect(jsonPath("$.nickname").value("nickname"));
  }


  @Test
  @DisplayName("사용자 단일 조회 실패 404")
  void getMemberNotFound() throws Exception {
    // given
    Long memberId = 999L;
    given(memberService.findById(memberId))
        .willThrow(new CustomException(ErrorCode.USER_NOT_FOUND));

    // when & then
    mockMvc.perform(get("/api/users/{memberId}", memberId)
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isNotFound());
  }

  @Test
  @DisplayName("사용자 닉네임 업데이트 성공")
  void updateNicknameSuccess() throws Exception {
    // given
    Long memberId = 1L;
    String newNickname = "newNickname";

    Member member = new Member("test@test.com", "newNickname", "a1234567", false);

    MemberUpdateResult updateResult = new MemberUpdateResult(
        1L,
        "test@test.com",
        "newNickname",
        null
    );

    MemberUpdateResponse response = new MemberUpdateResponse(
        1L,
        "test@test.com",
        "newNickname",
        null
    );

    // 서비스 & 매퍼 모킹
    given(memberService.update(memberId, newNickname)).willReturn(updateResult);
//    given(memberMapper.toMemberUpdateResult(member)).willReturn(updateResult);
    given(memberMapper.toMemberUpdateResponse(updateResult)).willReturn(response);

    // when & then
    mockMvc.perform(patch("/api/users/{memberId}", memberId)
            .contentType(MediaType.APPLICATION_JSON)
            .content(newNickname)) // 단일 문자열이므로 쌍따옴표 포함
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(memberId))
        .andExpect(jsonPath("$.email").value("test@test.com"))
        .andExpect(jsonPath("$.nickname").value(newNickname));
  }

}
