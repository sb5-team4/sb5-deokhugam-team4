package com.codeit.deokhugam.repository;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import com.codeit.deokhugam.common.exception.handler.CustomException;
import com.codeit.deokhugam.common.exception.handler.ErrorCode;
import com.codeit.deokhugam.domain.entity.Member;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@DataJpaTest
public class MemberRepositoryTest extends DataBaseConnectionSupport {

  @Autowired
  private MemberRepository memberRepository;

  @Autowired
  EntityManager em;

  Member member;
  Member updateMember;
  private BCryptPasswordEncoder passwordEncoder;

  @BeforeEach
  public void setup() {
    passwordEncoder = new BCryptPasswordEncoder();

    String rawPassword = "a1234567";
    String encodedPassword = passwordEncoder.encode(rawPassword);

    member = Member.builder()
        .email("test@test.com")
        .nickname("user1")
        .password(encodedPassword)
        .deleted(false)
        .build();
    updateMember = Member.builder()
        .email("test@test.com")
        .nickname("user2")
        .password(encodedPassword)
        .deleted(false)
        .build();
  }

  @Test
  @DisplayName("멤버 생성 테스트")
  public void createMember() {
    memberRepository.save(member);
    em.flush();
    em.clear();

    // 조회
    Member found = memberRepository.findByEmail(member.getEmail())
        .orElseThrow(() -> new RuntimeException("존재하지 않는 이메일입니다."));
    ;

    // 검증
    assertNotNull(found);
    assertEquals(member.getEmail(), found.getEmail());
    assertEquals(member.getNickname(), found.getNickname());
    assertFalse(found.isDeleted());

  }

  @Test
  @DisplayName("로그인 테스트 - 이메일과 비밀번호 확인")
  public void loginMember() {
    memberRepository.save(member);
    em.flush();
    em.clear();

    Member found = memberRepository.findByEmail(member.getEmail())
        .orElseThrow(() -> new RuntimeException("존재하지 않는 이메일입니다."));
    assertNotNull(found);

    // 평문 비밀번호와 DB 해시 비교
    assertTrue(passwordEncoder.matches("a1234567", found.getPassword()));
  }

  @Test
  @DisplayName("조회(단건)")
  public void findMemberById() {
    memberRepository.save(member);
    Member found = memberRepository.findById(member.getId())
        .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
    assertNotNull(found);
    em.flush();
    em.clear();
  }

  @Test
  @DisplayName("업데이트 닉네임")
  public void updateMember() {
    // given
    memberRepository.save(member);
    Long memberId = member.getId();
    String newNickname = "user2";

    // when
    Member findMember = memberRepository.findById(memberId)
        .orElseThrow(() -> new RuntimeException("Member not found"));
    findMember.updateNickname(newNickname);
    memberRepository.save(findMember);

    // then
    Member updated = memberRepository.findById(memberId)
        .orElseThrow();
    assertEquals(newNickname, updated.getNickname());
  }

}
