package com.codeit.deokhugam.batch.powerMember;

import com.codeit.deokhugam.batch.powerMember.dto.PowerMemberScoreDto;
import com.codeit.deokhugam.domain.entity.PowerMember;
import com.codeit.deokhugam.repository.MemberRepository;
import com.codeit.deokhugam.repository.ReviewRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;


@Configuration
@EnableBatchProcessing
@RequiredArgsConstructor
public class PowerMemberJobConfig {

  private final JobRepository jobRepository;
  private final PlatformTransactionManager transactionManager;
  private final PowerMemberWriter powerMemberWriter;
  private final PowerMemberRepository powerMemberRepository;
  private final ReviewRepository reviewRepository;
  private final MemberRepository memberRepository;

  @Bean
  public Job powerMemberJob(Step powerMemberClearStep, Step powerMemberStep,
      Step powerMemberRankingStep) {
    return new JobBuilder("powerMemberJob", jobRepository)
        .incrementer(new RunIdIncrementer())
        .start(powerMemberClearStep)
        .next(powerMemberStep)
        .next(powerMemberRankingStep)
        .build();
  }

  @Bean
  public Step powerMemberClearStep() {
    return new StepBuilder("powerMemberClearStep", jobRepository)
        .tasklet((contribution, chunkContext) -> {
          powerMemberRepository.deleteAllInBatch();
          return RepeatStatus.FINISHED;
        }, transactionManager)
        .build();
  }

  @Bean
  public Step powerMemberStep(PowerMemberReader powerMemberReader) {
    return new StepBuilder("powerMemberStep", jobRepository)
        .<PowerMemberScoreDto, PowerMember>chunk(100, transactionManager)
        .reader(powerMemberReader)
        .processor(new PowerMemberProcessor(memberRepository))
        .writer(powerMemberWriter)
        .build();
  }

  @Bean
  @StepScope
  public PowerMemberReader powerMemberReader() {
    return new PowerMemberReader(reviewRepository);
  }

  @Bean
  public Step powerMemberRankingStep() {
    return new StepBuilder("powerMemberRankingStep", jobRepository)
        .tasklet((contribution, chunkContext) -> {
          String[] periods = {"DAILY", "WEEKLY", "MONTHLY", "ALL_TIME"};
          for (String period : periods) {
            List<PowerMember> members = powerMemberRepository.findAllByPeriodOrderByScoreDesc(
                period);
            long rank = 1;
            for (PowerMember m : members) {
              m.setRank(rank++);
            }
            powerMemberRepository.saveAll(members);
          }
          return RepeatStatus.FINISHED;
        }, transactionManager)
        .build();
  }
}
