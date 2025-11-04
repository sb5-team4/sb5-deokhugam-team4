package com.codeit.deokhugam.batch.notification.config;

import com.codeit.deokhugam.batch.common.BatchCustomException;
import com.codeit.deokhugam.batch.common.BatchErrorCode;
import com.codeit.deokhugam.batch.notification.NotificationReader;
import com.codeit.deokhugam.batch.notification.NotificationWriter;
import com.codeit.deokhugam.domain.entity.Notification;
import com.codeit.deokhugam.repository.NotificationRepository;
import jakarta.persistence.EntityManagerFactory;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JpaPagingItemReader;
import org.springframework.batch.item.database.builder.JpaPagingItemReaderBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class NotificationBatchConfig {

  private final JobRepository jobRepository;
  private final PlatformTransactionManager transactionManager;

  private final NotificationReader notificationReader;
  private final NotificationWriter notificationWriter;

  private final EntityManagerFactory entityManagerFactory;
  private final NotificationRepository notificationRepository;

  private static final int CHUNK_SIZE = 1000;

  //Job 정의
  @Bean
  public Job deleteOldNotificationsJob(Step deleteOldNotificationsStep) {
    return new JobBuilder("deleteOldNotificationsJob", jobRepository)
        .start(deleteOldNotificationsStep)
        .build();
  }

  //Step 정의
  @Bean
  public Step deleteOldNotificationsStep() {
    return new StepBuilder("deleteOldNotificationsStep", jobRepository)
        .<Notification, Notification>chunk(CHUNK_SIZE, transactionManager)
        .reader(notificationReader) // ★ 4. 주입받은 Bean 사용
        .writer(notificationWriter) // ★ 5. 주입받은 Bean 사용
        // (Processor는 필요 없으므로 생략)
        .build();
  }

  // ItemReader 정의
  @Bean
  public JpaPagingItemReader<Notification> oldNotificationReader() {

    Instant cutoffDate = Instant.now().minus(7, ChronoUnit.DAYS);
    log.info("Batch Reader: {} 이전의 확인된(confirmed) 알림을 조회합니다.", cutoffDate);

    return new JpaPagingItemReaderBuilder<Notification>()
        .name("oldNotificationReader")
        .entityManagerFactory(entityManagerFactory)
        .pageSize(CHUNK_SIZE)
        // confirmed=true 알림 중 1주일이 경과된 알림"
        .queryString(
            "SELECT n FROM Notification n WHERE n.confirmed = true AND n.updatedAt < :cutoffDate"
        )
        .parameterValues(Map.of("cutoffDate", cutoffDate))
        .build();
  }

  //ItemWriter (쓰기) 정의
  @Bean
  public ItemWriter<Notification> notificationDeleter() {
    return chunk -> {
      List<Long> notificationIds = chunk.getItems().stream()
          .map(Notification::getId)
          .collect(Collectors.toList());

      try {
        // DB 삭제 시도
        notificationRepository.deleteAllByIdInBatch(notificationIds);
        log.info("Batch Writer: {}개의 오래된 알림을 ID로 물리 삭제합니다.", notificationIds.size());

      } catch (Exception e) {
        // DB 조회 실패 시 예외
        log.error("알림 삭제 배치 ItemWriter 실패: {}", e.getMessage());
        throw new BatchCustomException(
            BatchErrorCode.NOTIFICATION_DELETION_FAILED,
            e.getMessage() // (동적 메시지 전달: "...오류가 발생했습니다: [DB 오류 메시지]")
        );
      }
    };
  }
}