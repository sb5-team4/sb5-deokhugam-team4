package com.codeit.deokhugam.repository;

import com.codeit.deokhugam.domain.entity.Notification;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface NotificationRepository
    extends JpaRepository<Notification, Long>, NotificationQueryRepository {

  Optional<Notification> findByIdAndDeletedIsFalse(Long id);

  List<Notification> findByMemberId(Long memberId);
  
  @Modifying
  @Query("DELETE FROM Notification n WHERE n.deleted = true AND n.updatedAt < :updatedAt")
  void hardDeleteAllBefore(@Param("updatedAt") Instant updatedAt);
  
  // 알림 삭제 배치의 Reader가 사용할 쿼리
  @Query(
      "SELECT n FROM Notification n " +
          "WHERE n.confirmed = true AND n.updatedAt < :cutoffDate"
  )
  List<Notification> findOldConfirmedNotifications(@Param("cutoffDate") Instant cutoffDate);

}
