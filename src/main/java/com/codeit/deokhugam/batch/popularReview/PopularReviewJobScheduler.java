package com.codeit.deokhugam.batch.popularReview;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class PopularReviewJobScheduler {

  private final JobLauncher jobLauncher;
  private final Job popularReviewJob;

  // 애플리케이션 시작 시 1회 실행
  @PostConstruct
  public void runOnStartup() {
    runPopularReviewJob();
  }

  //   1분마다 실행 (cron: "0 */1 * * * *")
//  @Scheduled(cron = "0 */1 * * * *", zone = "Asia/Seoul")
  // 매일 새벽 1시 실행
  @Scheduled(cron = "0 0 1 * * *", zone = "Asia/Seoul")
  public void runPopularReviewJob() {
    try {
      JobParameters jobParameters = new JobParametersBuilder()
          .addLong("time", System.currentTimeMillis()) // JobInstance 구분용
          .toJobParameters();

      jobLauncher.run(popularReviewJob, jobParameters);
      log.info(" ✅ PopularReviewJob executed successfully!");
    } catch (Exception e) {
      log.info("❌ PopularReviewJob failed: {}", e.getMessage());
    }
  }
}
