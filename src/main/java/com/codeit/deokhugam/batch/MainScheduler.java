package com.codeit.deokhugam.batch;

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
public class MainScheduler {

  private final JobLauncher jobLauncher;
  private final Job popularReviewJob;
  private final Job hardDeleteJob;


  // 애플리케이션 시작 시 1회 실행
  @PostConstruct
  public void runOnStartup() {
    runOrderedJobs();
  }

  //  매일 새벽 1시 실행
  @Scheduled(cron = "0 0 1 * * *", zone = "Asia/Seoul")
  public void runBatches() {

    runOrderedJobs();
  }

  private void runOrderedJobs() {
    JobParameters jobParameters = new JobParametersBuilder()
        .addLong("time", System.currentTimeMillis()) // JobInstance 구분용
        .toJobParameters();

    // 1. hardDeleteJob
    try {
      jobLauncher.run(hardDeleteJob, jobParameters);

    } catch (Exception e) {
      log.error("hardDeleteJob failed : {}", e.getMessage());
    }

    // 2. popularReviewJob
    try {
      jobLauncher.run(popularReviewJob, jobParameters);

    } catch (Exception e) {
      log.error("popularReviewJob failed : {}", e.getMessage());
    }

  }

}
