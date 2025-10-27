package com.codeit.deokhugam.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import com.codeit.deokhugam.common.exception.handler.CustomException;
import com.codeit.deokhugam.common.exception.handler.ErrorCode;
import com.codeit.deokhugam.domain.entity.Member;
import com.codeit.deokhugam.dto.command.member.MemberCreateCommand;
import com.codeit.deokhugam.dto.command.member.MemberLoginCommand;
import com.codeit.deokhugam.dto.result.member.MemberCreatedResult;
import com.codeit.deokhugam.dto.result.member.MemberFindResult;
import com.codeit.deokhugam.dto.result.member.MemberLoginResult;
import com.codeit.deokhugam.dto.result.member.MemberUpdateResult;
import com.codeit.deokhugam.mapper.MemberMapper;
import com.codeit.deokhugam.repository.MemberRepository;
import com.codeit.deokhugam.service.impl.MemberService;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
public class MemberServiceTest {

  @Mock
  private MemberRepository memberRepository;

  @Mock
  private MemberMapper memberMapper;

  @Mock
  private PasswordEncoder passwordEncoder;

  @InjectMocks
  private MemberService memberService;

  private long id;
  private String nickname;
  private String email;
  private String password;
  private Member member;

  @BeforeEach
  void setUp() {
    id = 1L;
    nickname = "testUser";
    email = "test@example.com";
    password = "password123";

    member = new Member(email, nickname, password, false);
  }

  @Test
  @DisplayName("사용자 생성 성공")
  void createMemberSuccess() {
    // given
    MemberCreateCommand command = new MemberCreateCommand(email, nickname, password);
    MemberCreatedResult result = new MemberCreatedResult(id, nickname, email, null);
    given(memberRepository.existsByEmail(eq(email))).willReturn(false);
    given(memberRepository.existsByNickname(eq(nickname))).willReturn(false);
    given(memberRepository.save(any(Member.class))).willReturn(member);
    given(memberMapper.toMember(any(MemberCreateCommand.class))).willReturn(member);
    given(memberMapper.toMemberCreatedResult(any(Member.class))).willReturn(result);
    given(passwordEncoder.encode(anyString())).willReturn("password123");

    // when
    MemberCreatedResult result2 = memberService.create(command);

    // then
    assertThat(result2).isEqualTo(result);
    verify(memberRepository).save(any(Member.class));
  }

  @Test
  @DisplayName("사용자 생성 실패 테스트 - 이메일 중복")
  void createMemberFailureEmailDuplicate() throws Exception {
    // given
    MemberCreateCommand command = new MemberCreateCommand(email, nickname, password);

    // 이메일 존재한다고 stub
    given(memberRepository.existsByEmail(eq(email))).willReturn(true);

    // when & then
    RuntimeException exception = assertThrows(RuntimeException.class, () -> {
      memberService.create(command);
    });

    assertThat(exception.getMessage()).isEqualTo("Email already exists");

    // nickname 중복은 확인 안 함 (이메일에서 먼저 걸리므로)
    verify(memberRepository, never()).save(any(Member.class));
  }

  @Test
  @DisplayName("로그인 성공 테스트")
  void login_Success() {
    // given
    MemberLoginCommand command = new MemberLoginCommand(email, password);
    Member member = new Member(email, nickname, "encodedPassword", false);

    // 레포지토리 stub
    given(memberRepository.existsByEmail(eq(email))).willReturn(true);
    given(memberRepository.findByEmail(eq(email))).willReturn(Optional.of(member));

    // 패스워드 encoder stub
    given(passwordEncoder.matches(eq(password), eq(member.getPassword()))).willReturn(true);

    // mapper stub
    MemberLoginResult loginResult = new MemberLoginResult(id, nickname, email, null);
    given(memberMapper.toMemberLoginResult(member)).willReturn(loginResult);

    // when
    MemberLoginResult result = memberService.login(command);

    // then
    assertThat(result).isEqualTo(loginResult);
  }

  @Test
  @DisplayName("로그인 실패 테스트 - 비밀번호 불일치")
  void login_Failure_WrongPassword() {
    // given
    MemberLoginCommand command = new MemberLoginCommand(email, "wrongPassword");
    Member member = new Member(email, nickname, "encodedPassword", false);

    // 레포지토리 stub
    given(memberRepository.existsByEmail(eq(email))).willReturn(true);
    given(memberRepository.findByEmail(eq(email))).willReturn(Optional.of(member));

    // 패스워드 매치 실패
    given(passwordEncoder.matches(eq(command.password()), eq(member.getPassword()))).willReturn(
        false);

    // when & then
    RuntimeException exception = assertThrows(RuntimeException.class, () -> {
      memberService.login(command);
    });

    assertThat(exception.getMessage()).isEqualTo("Wrong password");
  }

  @Test
  @DisplayName("조회 단건")
  void findMemberById_Success() {
    // given
    Long memberId = 1L;
    MemberFindResult expectedResult = new MemberFindResult(memberId, "test@test.com", "nickname",
        null);

    given(memberRepository.findById(memberId)).willReturn(Optional.of(member));
    given(memberMapper.toMemberFindResult(member)).willReturn(expectedResult);

    // when
    MemberFindResult result = memberService.findById(memberId);

    // then
    assertThat(result).isNotNull();
    assertThat(result.id()).isEqualTo(memberId);
    assertThat(result.email()).isEqualTo("test@test.com");
    assertThat(result.nickname()).isEqualTo("nickname");
  }

  @Test
  @DisplayName("회원 단건 조회 실패 - 사용자 없음")
  void findById_Fail_UserNotFound() {
    // given
    Long memberId = 999L;
    given(memberRepository.findById(memberId)).willReturn(Optional.empty());

    // when & then
    CustomException exception = assertThrows(CustomException.class,
        () -> memberService.findById(memberId));

    assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.USER_NOT_FOUND.getCode());
  }

  @Test
  @DisplayName("닉네임 업데이트 성공")
  void updateNickname_Success() {
    // given
    Long memberId = 1L;
    String newNickname = "newNickname";

    MemberUpdateResult updateResult = new MemberUpdateResult(memberId, "test@test.com", newNickname,
        null);

    given(memberRepository.findById(memberId)).willReturn(Optional.of(member));
    given(memberMapper.toMemberUpdateResult(member)).willReturn(updateResult);

    // when
    MemberUpdateResult result = memberService.update(memberId, newNickname);

    // then
    assertThat(result).isNotNull();
    assertThat(result.nickname()).isEqualTo(newNickname);

    // member.updateNickname가 호출됐는지 확인
    assertThat(member.getNickname()).isEqualTo(newNickname);

    // memberRepository.save 호출 검증
    verify(memberRepository).save(member);
  }

  @Test
  @DisplayName("닉네임 업데이트 실패 - 사용자 없음")
  void updateNickname_Fail_UserNotFound() {
    // given
    Long memberId = 999L;
    String newNickname = "newNickname";

    given(memberRepository.findById(memberId)).willReturn(Optional.empty());

    // when & then
    CustomException exception = assertThrows(CustomException.class,
        () -> memberService.update(memberId, newNickname));

    assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.USER_NOT_FOUND.getCode());
  }

}
