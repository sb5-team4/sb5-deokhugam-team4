package com.codeit.deokhugam.service.book;

import com.codeit.deokhugam.common.exception.handler.CustomException;
import com.codeit.deokhugam.common.exception.handler.ErrorCode;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class IsbnOcrService {

  private static final String OCR_API_URL = "https://api.ocr.space/parse/image";
  private static final Pattern ISBN_PATTER = Pattern.compile("(97[89]\\d{10})");

  private final RestTemplate restTemplate;
  private final ObjectMapper objectMapper;

  @Value("${OCR_API_KEY:mock-key}")
  private String ocrApiKey;

  /**
   * 이미지에서 ISBN을 추출
   *
   * @param image OCR 처리할 이미지 파일
   * @return 추출된 ISBN 번호
   */
  public String extractIsbnFromImage(MultipartFile image) {
    validateImage(image);

    String extractedText = callOcrApi(image);

    String isbn = parseIsbn(extractedText);

    return isbn;
  }

  /**
   * 이미지 파일 유효성 검증
   *
   * @param image 검증할 이미지 파일
   */
  private void validateImage(MultipartFile image) {
    if (image == null || image.isEmpty()) {
      throw new CustomException(ErrorCode.INVALID_FILE);
    }

    String contentType = image.getContentType();
    if (contentType == null || !contentType.startsWith("image/")) {
      throw new CustomException(ErrorCode.INVALID_FILE);
    }

    long maxSize = 10 * 1024 * 1024; // 10MB
    if (image.getSize() > maxSize) {
      throw new CustomException(ErrorCode.INVALID_FILE);
    }
  }

  /**
   * OCR Space API 호출
   *
   * @param image OCR 처리할 이미지
   * @return OCR 결과 텍스트
   */
  private String callOcrApi(MultipartFile image) {
    try {
      HttpHeaders headers = new HttpHeaders();
      headers.setContentType(MediaType.MULTIPART_FORM_DATA);
      headers.set("apikey", ocrApiKey);

      MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
      body.add("file", image.getResource());
      body.add("language", "eng");
      body.add("isOverlayRequired", "false");
      body.add("detectOrientation", "true");
      body.add("scale", "true");
      body.add("OCREngine", "2");

      HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

      ResponseEntity<String> response = restTemplate.exchange(
          OCR_API_URL,
          HttpMethod.POST,
          requestEntity,
          String.class
      );

      return parseOcrResponse(response.getBody());

    } catch (Exception e) {
      throw new CustomException(ErrorCode.FILE_UPLOAD_FAILED);
    }
  }

  /**
   * OCR API 응답 파싱
   *
   * @param responseBody API 응답 바디
   * @return 추출된 텍스트
   */
  private String parseOcrResponse(String responseBody) {
    try {
      JsonNode root = objectMapper.readTree(responseBody);

      boolean isErroredOnProcessing = root.path("IsErroredOnProcessing").asBoolean();
      if (isErroredOnProcessing) {
        throw new CustomException(ErrorCode.INVALID_FILE);
      }

      JsonNode parsedResults = root.path("ParsedResults");
      if (parsedResults.isEmpty()) {
        throw new CustomException(ErrorCode.INVALID_FILE);
      }

      String parsedText = parsedResults.get(0).path("ParsedText").asText();

      return parsedText;

    } catch (IOException e) {
      throw new CustomException(ErrorCode.FILE_UPLOAD_FAILED);
    }
  }

  /**
   * 텍스트에서 ISBN 추출
   *
   * @param text OCR로 추출된 텍스트
   * @return ISBN 번호 (13자리)
   */
  private String parseIsbn(String text) {
    String cleanedText = text.replaceAll("[\\s\\-]", "");

    Matcher matcher = ISBN_PATTER.matcher(cleanedText);

    if (matcher.find()) {
      return matcher.group(1);
    }

    throw new CustomException(ErrorCode.ISBN_NOT_COLLECT);

  }
}
