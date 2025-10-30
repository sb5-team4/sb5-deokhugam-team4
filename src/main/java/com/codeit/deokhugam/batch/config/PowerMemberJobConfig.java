package com.codeit.deokhugam.batch.config;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Slf4j
@Configuration
@EnableBatchProcessing
@RequiredArgsConstructor
public class PowerMemberJobConfig {

  private final JobRepository jobRepository;
  private final PlatformTransactionManager transactionManager;

  @Bean
  public Job ExampleJob() {
    return new JobBuilder("exampleJob", jobRepository) //JobBuilder → exampleJob이라는 이름의 Job을 만듦
        .start(Step()) //실행할 첫 번째 Step을 등록
        .build();
  }

  @Bean
  public Step Step() {
    return new StepBuilder("step", jobRepository) //step이라는 이름의 Step 생성
        //단일 작업 단위(Tasklet) 등록
        .tasklet((contribution, chunkContext) -> { //Step 실행 컨텍스트
          log.info("Step!!!!"); //콘솔에 “Step!!!!” 로그 출력
          return RepeatStatus.FINISHED; //Step이 정상 종료되었다는 신호
        }, transactionManager)  //트랜잭션 관리 (롤백, 커밋 담당)
        .build();
  }
}







