package com.codeit.deokhugam.batch.popularReview.config;


import static com.codeit.deokhugam.domain.enums.Period.ALL_TIME;
import static com.codeit.deokhugam.domain.enums.Period.DAILY;
import static com.codeit.deokhugam.domain.enums.Period.MONTHLY;
import static com.codeit.deokhugam.domain.enums.Period.WEEKLY;

import com.codeit.deokhugam.batch.popularReview.PopularReviewProcessor;
import com.codeit.deokhugam.batch.popularReview.PopularReviewReader;
import com.codeit.deokhugam.batch.popularReview.PopularReviewWriter;
import com.codeit.deokhugam.domain.entity.PopularReview;
import com.codeit.deokhugam.domain.entity.Review;
import com.codeit.deokhugam.repository.PopularReviewRepository;
import java.time.Instant;
import java.util.List;
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

@Configuration // absract class OR interface 둘중 모듈화 해줘야함
@EnableBatchProcessing
@RequiredArgsConstructor
@Slf4j
public class PopularReviewBatchConfig {

  private final PlatformTransactionManager transactionManager;
  private final PopularReviewReader popularReviewReader;
  private final PopularReviewProcessor popularReviewProcessor;
  private final PopularReviewWriter popularReviewWriter;
  private final PopularReviewRepository popularReviewRepository;

  @Bean
  public JobExecutionListener popularReivewJobExecutionListener() {

    return new JobExecutionListener() {
      @Override
      public void beforeJob(@NonNull JobExecution jobExecution) {
        Instant now = Instant.now();
        long chunkCount = 0;
        jobExecution.getExecutionContext().put("jobStartTime", now);
        jobExecution.getExecutionContext().put("chunkCount", chunkCount);
        log.info("Job execution started at {}", now);
      }

      @Override
      public void afterJob(@NonNull JobExecution jobExecution) {
        log.info("Job execution ended at {}", Instant.now());
      }

    };

  }

  @Bean
  public Job popularReviewJob(JobRepository jobRepository) {
    return new JobBuilder("popular-review-job", jobRepository)
        .start(truncatePopularReviewStep(jobRepository, transactionManager))
        .next(setScorePopularReviewStep(jobRepository, transactionManager))
        .next(popularReviewRankingStep(jobRepository, transactionManager))
        .listener(popularReivewJobExecutionListener())
        .build();
  }

  // 1. PopularReview 테이블 초기화 Step
  @Bean
  public Step truncatePopularReviewStep(JobRepository jobRepository,
      PlatformTransactionManager transactionManager) {
    return new StepBuilder("truncate-popular-review-step", jobRepository)
        .tasklet((contribution, chunkContext) -> {
          log.info("Truncating PopularReview table...");
          popularReviewRepository.deleteAllInBatch();
          // or 직접 truncate 쿼리 날리기 (성능이 더 좋음)
          // popularReviewRepository.truncateTable();
          log.info("PopularReview table cleared.");
          return RepeatStatus.FINISHED;
        }, transactionManager)
        .build();
  }

  @Bean
  public Step setScorePopularReviewStep(JobRepository jobRepository,
      PlatformTransactionManager transactionManager) {
    return new StepBuilder("SetScorePopularReviewStep", jobRepository)
        .<Review, List<PopularReview>>chunk(1000, transactionManager) // 롤백 되었을 때 대처?
        .reader(popularReviewReader)
        .processor(popularReviewProcessor)
        .writer(popularReviewWriter)
        .build();
  }

  @Bean
  public Step popularReviewRankingStep(JobRepository jobRepository,
      PlatformTransactionManager transactionManager) {
    return new StepBuilder("ranking-popular-review-step", jobRepository)
        .tasklet((contribution, chunkContext) -> {
          log.info("Update Rank PopularReview table...");

          popularReviewRepository.updateRankByPeriod(ALL_TIME.name());
          popularReviewRepository.updateRankByPeriod(MONTHLY.name());
          popularReviewRepository.updateRankByPeriod(WEEKLY.name());
          popularReviewRepository.updateRankByPeriod(DAILY.name());

          log.info("Update Rank PopularReview table Complete");
          return RepeatStatus.FINISHED;
        }, transactionManager)
        .build();
  }
}
