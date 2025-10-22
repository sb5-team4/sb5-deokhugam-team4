package com.codeit.deokhugam.repository;

import com.codeit.deokhugam.domain.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member, Long> {
  
  boolean existsByEmail(String email);

  boolean existsByNickname(String nickname);

}
