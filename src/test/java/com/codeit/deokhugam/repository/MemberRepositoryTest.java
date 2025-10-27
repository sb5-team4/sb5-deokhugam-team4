package com.codeit.deokhugam.repository;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import com.codeit.deokhugam.domain.entity.Member;
import jakarta.persistence.EntityManager;
import java.util.Optional;
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
  @DisplayName("회원 단건 조회가 정상적으로 수행된다")
  void findById_Success() {
    // given
    Member saved = memberRepository.save(member);

    // when
    Optional<Member> found = memberRepository.findById(saved.getId());

    // then
    assertThat(found).isPresent();
    assertThat(found.get().getEmail()).isEqualTo("test@test.com");
  }

  @Test
  @DisplayName("회원 닉네임 수정이 DB에 반영된다")
  void updateNickname_Success() {
    // given
    memberRepository.save(member);
    em.flush();
    em.clear();

    // when
    Member findMember = memberRepository.findById(member.getId()).get();
    findMember.updateNickname("newNick");
    memberRepository.save(findMember);
    em.flush();
    em.clear();

    // then
    Member updated = memberRepository.findById(member.getId()).get();
    assertThat(updated.getNickname()).isEqualTo("newNick");
  }

}
