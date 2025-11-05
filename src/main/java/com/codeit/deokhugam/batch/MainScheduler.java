package com.codeit.deokhugam.batch;

import com.codeit.deokhugam.batch.log.LogUploadService;
import jakarta.annotation.PostConstruct;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
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
  private final Job popularBookJob;
  private final LogUploadService logUploadService;


  // 애플리케이션 시작 시 1회 실행
  @PostConstruct
  public void runOnStartup() {
    log.info("===== 애플리케이션 시작: 배치 작업 실행 시작 =====");
    runOrderedJobs();
    log.info("===== 애플리케이션 시작: 배치 작업 실행 완료 =====");
  }

  //  매일 새벽 1시 실행
  @Scheduled(cron = "0 0 1 * * *", zone = "Asia/Seoul")
  public void runBatches() {
    log.info("===== 스케줄 실행: 배치 작업 실행 시작 =====");
    runOrderedJobs();
    log.info("===== 스케줄 실행: 배치 작업 실행 완료 =====");
  }

  private void runOrderedJobs() {
    JobParameters jobParameters = new JobParametersBuilder()
        .addLong("time", System.currentTimeMillis()) // JobInstance 구분용
        .toJobParameters();

    // 1. hardDeleteJob
    try {
      log.info(">>> hardDeleteJob 시작");
      JobExecution execution = jobLauncher.run(hardDeleteJob, jobParameters);
      log.info("✅ hardDeleteJob 완료 - 상태: {}", execution.getStatus());

    } catch (Exception e) {
      log.error("❌ hardDeleteJob 실패: {}", e.getMessage(), e);
    }

    // 2. popularReviewJob
    try {
      log.info(">>> popularReviewJob 시작");
      JobExecution execution = jobLauncher.run(popularReviewJob, jobParameters);
      log.info("✅ popularReviewJob 완료 - 상태: {}", execution.getStatus());

    } catch (Exception e) {
      log.error("❌ popularReviewJob 실패: {}", e.getMessage(), e);
    }

    // 3. popularBookJob
    try {
      log.info(">>> popularBookJob 시작");
      JobExecution execution = jobLauncher.run(popularBookJob, jobParameters);
      log.info("✅ popularBookJob 완료 - 상태: {}", execution.getStatus());

    } catch (Exception e) {
      log.error("❌ popularBookJob 실패: {}", e.getMessage(), e);
    }

    // 4. 로그 파일 S3 업로드
    try {
      log.info(">>> 로그 파일 S3 업로드 시작");

      LocalDate yesterday = LocalDate.now().minusDays(1);
      String targetDate = yesterday.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

      log.info("업로드 대상 날짜: {}", targetDate);

      logUploadService.uploadLogFilesToS3(targetDate);

      log.info("✅ 로그 파일 S3 업로드 완료");

    } catch (Exception e) {
      log.error("❌ 로그 파일 S3 업로드 실패: {}", e.getMessage(), e);
    }
  }

}
