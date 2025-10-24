package com.codeit.deokhugam.common.exception.handler;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public class CustomException extends RuntimeException {

  private final HttpStatus httpStatus; // Http 상태값
  private final int errorCode;  // 에러 코드
  private final String errorMessage;  // 에러 메시지

  public CustomException(ErrorCode errorCode) {
    this.httpStatus = errorCode.getHttpStatus();
    this.errorCode = errorCode.getCode();
    this.errorMessage = errorCode.getMessage();
  }
}