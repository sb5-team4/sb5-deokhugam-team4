package com.codeit.deokhugam.repository;

import com.codeit.deokhugam.domain.entity.Member;
import java.time.Instant;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MemberRepository extends JpaRepository<Member, Long> {

  boolean existsByEmail(String email);

  boolean existsByNickname(String nickname);

  Optional<Member> findByEmail(String email);

  Optional<Member> findByIdAndDeletedIsFalse(Long memberId);

  @Modifying
  @Query("DELETE FROM Member m WHERE m.deleted = true AND m.updatedAt < :updatedAt")
  void hardDeleteAllBefore(@Param("updatedAt") Instant updatedAt);

}
