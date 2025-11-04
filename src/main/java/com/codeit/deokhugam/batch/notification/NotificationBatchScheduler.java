package com.codeit.deokhugam.batch.notification;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationBatchScheduler {

  private final JobLauncher jobLauncher;
  private final Job deleteOldNotificationsJob;

  @PostConstruct
  public void runOnStartup() {
    log.info("애플리케이션 시작. deleteOldNotificationsJob 1회 실행...");
    runDeleteOldNotificationsJob();
  }

  //매일 새벽 4시에 오래된 알림 삭제 배치를 실행
  @Scheduled(cron = "0 0 4 * * *", zone = "Asia/Seoul")
  public void runDeleteOldNotificationsJob() {
    log.info("오래된 알림 삭제 배치(deleteOldNotificationsJob)를 시작합니다...");
    try {
      JobParameters jobParameters = new JobParametersBuilder()
          .addLong("time", System.currentTimeMillis())
          .toJobParameters();

      jobLauncher.run(deleteOldNotificationsJob, jobParameters);

      log.info(" ✅ DeleteOldNotificationsJob 성공");
    } catch (Exception e) {
      // BatchCustomException을 포함한 모든 예외 catch
      log.info("❌ DeleteOldNotificationsJob 실패: {}", e.getMessage());
    }
  }
}