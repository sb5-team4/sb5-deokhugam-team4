package com.codeit.deokhugam.batch.log;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

@Slf4j
@Service
@RequiredArgsConstructor
public class LogUploadService {

  private final S3Client s3Client;

  @Value("${aws.s3.bucket}")
  private String bucketName;

  private static final String LOG_DIRECTORY = ".logs";

  public void uploadLogFilesToS3(String targetDate) {
    List<File> logFiles = findLogFilesByDate(targetDate);

    if (logFiles.isEmpty()) {
      log.info("업로드할 로그 파일이 없습니다. 날짜: {}", targetDate);
      return;
    }

    log.info("업로드할 로그 파일 개수: {}", logFiles.size());

    int successCount = 0;
    for (File logFile : logFiles) {
      try {
        uploadFileToS3(logFile, targetDate);

        deleteLocalFile(logFile);
        successCount++;

      } catch (Exception e) {
        log.error("로그 파일 업로드 실패: {}", logFile.getName(), e);
      }
    }

    log.info("로그 파일 업로드 완료: 성공 {}/{}", successCount, logFiles.size());
  }

  private List<File> findLogFilesByDate(String targetDate) {
    List<File> result = new ArrayList<>();

    try {
      Path logDir = Paths.get(LOG_DIRECTORY);

      if (!Files.exists(logDir)) {
        log.warn("로그 디렉토리가 존재하지 않습니다: {}", LOG_DIRECTORY);
        return result;
      }

      try (Stream<Path> paths = Files.list(logDir)) {
        paths.filter(Files::isRegularFile)
            .filter(path -> path.toString().endsWith(".log"))
            .filter(path -> path.getFileName().toString().contains(targetDate))
            .forEach(path -> result.add(path.toFile()));
      }

      log.debug("조회된 로그 파일: {}", result.stream().map(File::getName).toList());

    } catch (IOException e) {
      log.error("로그 파일 조회 중 오류 발생", e);
    }

    return result;
  }

  private void uploadFileToS3(File logFile, String targetDate) throws IOException {
    String s3Key = String.format("logs/%s/%s", targetDate, logFile.getName());

    log.info("S3 업로드 시작: {} -> s3://{}/{}", logFile.getName(), bucketName, s3Key);

    PutObjectRequest putObjectRequest = PutObjectRequest.builder()
        .bucket(bucketName)
        .key(s3Key)
        .contentType("text/plain")
        .build();

    try (FileInputStream fis = new FileInputStream(logFile)) {
      s3Client.putObject(
          putObjectRequest,
          RequestBody.fromInputStream(fis, logFile.length())
      );
    }

    log.info("S3 업로드 완료: {}", s3Key);
  }

  private void deleteLocalFile(File logFile) {
    try {
      if (logFile.delete()) {
        log.info("로컬 로그 파일 삭제 완료: {}", logFile.getName());
      } else {
        log.warn("로컬 로그 파일 삭제 실패: {}", logFile.getName());
      }
    } catch (Exception e) {
      log.error("로컬 로그 파일 삭제 중 오류 발생: {}", logFile.getName(), e);
    }
  }
}