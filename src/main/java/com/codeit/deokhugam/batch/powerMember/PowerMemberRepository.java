package com.codeit.deokhugam.batch.powerMember;

import com.codeit.deokhugam.domain.entity.PowerMember;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PowerMemberRepository extends JpaRepository<PowerMember, Long> {

  // 필요하면 여기에 추가 쿼리 메서드 선언 가능
  // 예: 기간별로 삭제 후 재삽입하는 경우
  void deleteByPeriod(String period);

//  @Query("SELECT p FROM PowerMember p ORDER BY p.score DESC")
//  List<PowerMember> findAllOrderByScoreDesc();

  List<PowerMember> findAllByPeriodOrderByScoreDesc(String period);

}