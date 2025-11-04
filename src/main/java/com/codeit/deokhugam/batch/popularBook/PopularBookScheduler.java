package com.codeit.deokhugam.batch.popularBook;

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
public class PopularBookScheduler {

  private final JobLauncher jobLauncher;
  private final Job popularBookJob;

  /**
   * 테스트용 메서드
   */
//  @Scheduled(cron = "0 * * * * *")
//  public void runPopularBookBatch() throws Exception {
//    log.info("===== 인기 도서 배치 시작 =====");
//
//    JobParameters jobParameters = new JobParametersBuilder()
//        .addLong("timestamp", System.currentTimeMillis())
//        .toJobParameters();
//
//    jobLauncher.run(popularBookJob, jobParameters);
//
//    log.info("===== 인기 도서 배치 완료 =====");
//  }

  /**
   * 실제 배포용: 매일 새벽 4시에 실행
   */
  @Scheduled(cron = "0 0 4 * * *")  // 매일 새벽 4시 실행
  public void runPopularBookBatchDaily() throws Exception {
    log.info("===== 인기 도서 배치 시작 =====");
    JobParameters jobParameters = new JobParametersBuilder()
        .addLong("timestamp", System.currentTimeMillis())
        .toJobParameters();

    jobLauncher.run(popularBookJob, jobParameters);
    log.info("===== 인기 도서 배치 완료 =====");
  }
}