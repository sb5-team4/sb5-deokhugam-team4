package com.codeit.deokhugam.batch.notification;

import com.codeit.deokhugam.batch.common.BatchCustomException;
import com.codeit.deokhugam.batch.common.BatchErrorCode;
import com.codeit.deokhugam.domain.entity.Notification;
import com.codeit.deokhugam.repository.NotificationRepository;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationWriter implements ItemWriter<Notification> {

  private final NotificationRepository notificationRepository;

  @Override
  public void write(Chunk<? extends Notification> chunk) throws Exception {

    List<Long> notificationIds = chunk.getItems().stream()
        .map(Notification::getId)
        .collect(Collectors.toList());

    if (notificationIds.isEmpty()) {
      return;
    }

    try {
      // DB에서 ID 리스트를 사용하여 물리 삭제
      notificationRepository.deleteAllByIdInBatch(notificationIds);
      log.info("Batch Writer: {}개의 오래된 알림을 ID로 물리 삭제했습니다.", notificationIds.size());

    } catch (Exception e) {
      // DB 삭제 실패 시 예외
      log.error("알림 삭제 배치 ItemWriter 실패: {}", e.getMessage());

      BatchCustomException batchCustomEx = new BatchCustomException(
          BatchErrorCode.NOTIFICATION_DELETION_FAILED,
          e.getMessage()
      );
      throw new Exception(batchCustomEx);
    }
  }
}