package com.codeit.deokhugam.batch.popularBook;

import com.codeit.deokhugam.batch.popularBook.dto.PopularBookDto;
import com.codeit.deokhugam.domain.entity.PopularBook;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@RequiredArgsConstructor
@EnableScheduling
public class PopularBookBatchConfig {

  private final JobRepository jobRepository;
  private final PlatformTransactionManager transactionManager;
  private final PopularBookReader popularBookReader;
  private final PopularBookProcessor popularBookProcessor;
  private final PopularBookWriter bookWriter;
  private final PopularBookWriter popularBookWriter;

  @Bean
  public Job popularBookJob() {
    return new JobBuilder("popularBookJob", jobRepository)
        .start(popularBookStep())
        .build();
  }

  private Step popularBookStep() {
    return new StepBuilder("popularBookStep", jobRepository)
        .<PopularBookDto, PopularBook>chunk(100, transactionManager)
        .reader(popularBookReader)
        .processor(popularBookProcessor)
        .writer(popularBookWriter)
        .build();
  }
}
