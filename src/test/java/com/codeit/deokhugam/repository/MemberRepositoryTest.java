package com.codeit.deokhugam.repository;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

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
  }

  @Test
  @DisplayName("멤버 생성 테스트")
  public void createMember() {
    memberRepository.save(member);
    em.flush();
    em.clear();

    // 조회
    Member found = memberRepository.findByEmail(member.getEmail());

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

    Member found = memberRepository.findByEmail(member.getEmail());
    assertNotNull(found);

    // 평문 비밀번호와 DB 해시 비교
    assertTrue(passwordEncoder.matches("a1234567", found.getPassword()));
  }

}
