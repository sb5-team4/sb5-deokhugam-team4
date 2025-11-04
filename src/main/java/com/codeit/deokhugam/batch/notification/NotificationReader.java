package com.codeit.deokhugam.batch.notification;

import com.codeit.deokhugam.batch.common.BatchCustomException;
import com.codeit.deokhugam.batch.common.BatchErrorCode;
import com.codeit.deokhugam.domain.entity.Notification;
import com.codeit.deokhugam.repository.NotificationRepository;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Iterator;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.item.ItemReader;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@JobScope
@RequiredArgsConstructor
public class NotificationReader implements ItemReader<Notification> {

  private final NotificationRepository notificationRepository;
  private Iterator<Notification> notificationIterator;

  @Override
  public Notification read() throws Exception {
    try {
      //  DB에서 삭제 대상을 조회
      if (notificationIterator == null) {
        Instant cutoffDate = Instant.now().minus(7, ChronoUnit.DAYS);
        log.info("Batch Reader: {} 이전의 확인된(confirmed) 알림을 DB에서 조회합니다.", cutoffDate);

        List<Notification> oldNotifications =
            notificationRepository.findOldConfirmedNotifications(cutoffDate);

        this.notificationIterator = oldNotifications.iterator();
        log.info("Batch Reader: 총 {}개의 삭제 대상 알림을 찾았습니다.", oldNotifications.size());
      }

      if (notificationIterator.hasNext()) {
        return notificationIterator.next();
      } else {

        log.info("Batch Reader: 모든 알림 읽기 완료.");
        return null;
      }
    } catch (Exception e) {
      // DB 조회 실패 시 예외
      log.error("알림 조회 배치 ItemReader 실패: {}", e.getMessage());
      BatchCustomException batchCustomEx = new BatchCustomException(
          BatchErrorCode.NOTIFICATION_READ_FAILED, e.getMessage()
      );
      throw new Exception(batchCustomEx);
    }
  }
}