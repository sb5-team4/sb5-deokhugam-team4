package com.codeit.deokhugam.repository;

import com.codeit.deokhugam.domain.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

}
