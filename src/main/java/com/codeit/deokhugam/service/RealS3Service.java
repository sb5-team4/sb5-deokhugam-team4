package com.codeit.deokhugam.service;

import com.codeit.deokhugam.common.exception.handler.CustomException;
import com.codeit.deokhugam.common.exception.handler.ErrorCode;
import java.io.IOException;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

@Service
@RequiredArgsConstructor
public class RealS3Service implements S3Service {

  private final S3Client s3Client;

  @Value("${aws.s3.bucket}")
  private String bucketName;

  @Value("${aws.s3.base-url}")
  private String baseUrl;

  @Override
  public String uploadFile(MultipartFile file) {

    if (file == null || file.isEmpty()) {
      throw new CustomException(ErrorCode.INVALID_FILE);
    }

    String originalFilename = file.getOriginalFilename();

    String extension = originalFilename.substring(originalFilename.lastIndexOf('.'));
    String nameWithoutExtension = originalFilename.substring(0, originalFilename.lastIndexOf('.'));

    String FileName = nameWithoutExtension
        .replaceAll("[^a-zA-Z0-9가-힣]", "")
        .toLowerCase();

    String uploadedFileName = FileName + "_" + UUID.randomUUID().toString() + extension;

    try {
      PutObjectRequest putObjectRequest = PutObjectRequest.builder()
          .bucket(bucketName)
          .key(uploadedFileName)
          .contentType(file.getContentType())
          .build();

      s3Client.putObject(putObjectRequest,
          RequestBody.fromInputStream(file.getInputStream(), file.getSize())
      );

      return baseUrl + "/" + uploadedFileName;
    } catch (IOException e) {
      throw new CustomException(ErrorCode.FILE_UPLOAD_FAILED);
    }
  }

  @Override
  public void deleteFile(String fileUrl) {
    if (fileUrl == null || fileUrl.isEmpty()) {
      return;
    }

    try {
      String fileName = fileUrl.substring(fileUrl.lastIndexOf('/') + 1);
      DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
          .bucket(bucketName)
          .key(fileName)
          .build();

      s3Client.deleteObject(deleteObjectRequest);

    } catch (Exception ignored) {
    }
  }
}
