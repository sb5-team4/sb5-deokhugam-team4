package com.codeit.deokhugam.batch.notification.config;

import com.codeit.deokhugam.batch.notification.NotificationReader;
import com.codeit.deokhugam.batch.notification.NotificationWriter;
import com.codeit.deokhugam.domain.entity.Notification;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
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
        .reader(notificationReader) // 주입받은 Bean 사용
        .writer(notificationWriter) // 주입받은 Bean 사용
        // (변환할 필요가 없기 때문에 Processor는 생략)
        .build();
  }

}