package com.codeit.deokhugam.batch.log;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class LogUploadScheduler {

  private final LogUploadService logUploadService;

  /**
   * 매일 새벽 3시에 로그 파일 업로드 실행
   */
  @Scheduled(cron = "0 0 3 * * *", zone = "Asia/Seoul")
  public void uploadLogsToS3() {
    log.info("===== 로그 파일 S3 업로드 배치 시작 =====");

    try {
      LocalDate yesterday = LocalDate.now().minusDays(1);
      String targetDate = yesterday.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

      log.info("업로드 대상 날짜: {}", targetDate);

      logUploadService.uploadLogFilesToS3(targetDate);

      log.info("===== 로그 파일 S3 업로드 배치 완료 =====");

    } catch (Exception e) {
      log.error("로그 파일 S3 업로드 중 오류 발생", e);
    }
  }

  /**
   * 테스트용 메서드
   */
//  @Scheduled(cron = "0 * * * * *")
//  public void uploadLogsToS3ForTest() {
//    log.info("===== [테스트] 로그 파일 S3 업로드 배치 시작 =====");
//
//    try {
//      LocalDate today = LocalDate.now();
//      String targetDate = today.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
//
//      logUploadService.uploadLogFilesToS3(targetDate);
//
//      log.info("===== [테스트] 로그 파일 S3 업로드 배치 완료 =====");
//
//    } catch (Exception e) {
//      log.error("[테스트] 로그 파일 S3 업로드 중 오류 발생", e);
//    }
//  }
}