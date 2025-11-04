package com.codeit.deokhugam.batch.log;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class LogUploadScheduler {

  private final LogUploadService logUploadService;

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