package com.codeit.deokhugam.batch.common;

import lombok.Getter;

@Getter
public class BatchCustomException extends RuntimeException {

  private final int errorCode;         // 에러 코드
  private final String errorMessage;   // 에러 메시지

  public BatchCustomException(BatchErrorCode errorCode) {
    // 부모 클래스(RuntimeException)에 메시지 전달
    super(errorCode.getMessage());

    // 필드 초기화
    this.errorCode = errorCode.getCode();
    this.errorMessage = errorCode.getMessage();
  }

  // 동적 메시지를 보내기 위한 생성자
  public BatchCustomException(BatchErrorCode errorCode, Object... args) {

    super(String.format(errorCode.getMessage(), args));

    this.errorCode = errorCode.getCode();
    this.errorMessage = String.format(errorCode.getMessage(), args);
  }

}