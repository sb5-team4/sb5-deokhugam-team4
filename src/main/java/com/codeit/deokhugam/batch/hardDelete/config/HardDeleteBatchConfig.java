package com.codeit.deokhugam.batch.hardDelete.config;

import com.codeit.deokhugam.repository.CommentRepository;
import com.codeit.deokhugam.repository.MemberRepository;
import com.codeit.deokhugam.repository.NotificationRepository;
import com.codeit.deokhugam.repository.ReviewRepository;
import com.codeit.deokhugam.repository.book.BookRepository;
import java.time.Duration;
import java.time.Instant;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@EnableBatchProcessing
@RequiredArgsConstructor
@Slf4j
public class HardDeleteBatchConfig {

  private final PlatformTransactionManager transactionManager;
  private final BookRepository bookRepository;
  private final ReviewRepository reviewRepository;
  private final MemberRepository memberRepository;
  private final CommentRepository commentRepository;
  private final NotificationRepository notificationRepository;

  @Bean
  public JobExecutionListener hardDeleteJobExecutionListener() {

    return new JobExecutionListener() {
      @Override
      public void beforeJob(@NonNull JobExecution jobExecution) {
        Instant now = Instant.now();
        jobExecution.getExecutionContext().put("jobStartTime", now);
        log.info("Job execution started at {}", now);
      }

      @Override
      public void afterJob(@NonNull JobExecution jobExecution) {
        Instant startTime = (Instant) jobExecution.getExecutionContext().get("jobStartTime");
        Instant endTime = Instant.now();
        if (startTime != null) {
          Duration duration = Duration.between(startTime, endTime);
          log.info("⏱ Total execution time: {} seconds ({} ms)", duration.getSeconds(),
              duration.toMillis());
        }

      }

    };
  }


  @Bean
  public Job hardDeleteJob(JobRepository jobRepository) {
    return new JobBuilder("HardDeleteJob", jobRepository)
        .start(hardDeleteStep(jobRepository, transactionManager))
        .listener(hardDeleteJobExecutionListener())
        .build();
  }


  @Bean
  public Step hardDeleteStep(
      JobRepository jobRepository,
      PlatformTransactionManager transactionManager
  ) {
    return new StepBuilder("hardDeleteStep", jobRepository)
        .tasklet((contribution, chunkContext) -> {
          log.info("Truncating PopularReview table...");

          Instant cutoffTime = Instant.now().minusSeconds(24 * 60 * 60);

          bookRepository.hardDeleteAllBefore(cutoffTime);
          memberRepository.hardDeleteAllBefore(cutoffTime);
          reviewRepository.hardDeleteAllBefore(cutoffTime);
          commentRepository.hardDeleteAllBefore(cutoffTime);
          notificationRepository.hardDeleteAllBefore(cutoffTime);

          return RepeatStatus.FINISHED;
        }, transactionManager)
        .build();
  }
}
