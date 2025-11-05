package com.codeit.deokhugam.batch.powerMember;

import java.text.SimpleDateFormat;
import java.util.Date;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class PowerMemberScheduler {

  private final JobLauncher jobLauncher;
  private final Job powerMemberJob;
  private final JdbcTemplate jdbcTemplate;

  // 10초마다 실행
//  @Scheduled(fixedRate = 10000)
  //매일 오전 9시 실행
  @Scheduled(cron = "0 0 9 * * *")
  public void task1() {
    try {
// 실행 시점 기준 JobParameter 생성
      String timestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(new Date());
      JobParameters parameters = new JobParametersBuilder()
          .addString("run.timestamp", timestamp)
          .toJobParameters();
// Job 실행
      JobExecution execution = jobLauncher.run(powerMemberJob, parameters);
      log.info("==== Job Started: {}  ====", powerMemberJob.getName());
// 실행 중 상태 모니터링
      while (execution.isRunning()) {
        log.info("Job is still running...");
        Thread.sleep(1000);
      }
// 실행 결과 로그
      log.info("==== Job Finished ====");
      log.info("Exit Status      : {}", execution.getExitStatus());
      log.info("Job Instance ID   : {}", execution.getJobId());
//      log.info("Job Configuration : {}", execution.getJobConfigurationName());
      log.info("Last Updated     : {}", execution.getLastUpdated());
      log.info("Failure Exceptions: {}", execution.getFailureExceptions());
    } catch (Exception e) {
      log.error("Job execution failed", e);
    }
  }

}
