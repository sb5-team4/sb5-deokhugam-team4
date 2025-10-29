package com.codeit.deokhugam.repository;

import com.codeit.deokhugam.domain.entity.Notification;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

  Optional<Notification> findByIdAndDeletedIsFalse(Long id);

  List<Notification> findByMemberId(Long memberId);
}
