package com.codeit.deokhugam.service;

import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

// 실제 S3 설정 이전까지 사용할 더미 S3용 클래스
@Service
public class DummyS3Service implements S3Service {

  @Override
  public String uploadFile(MultipartFile file) {
    // 실제 업로드 없이 테스트용 URL만 반환
    return "https://dummy.url/" + UUID.randomUUID() + "_" + file.getOriginalFilename();
  }
}
