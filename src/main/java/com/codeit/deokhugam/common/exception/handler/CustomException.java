package com.codeit.deokhugam.common.exception.handler;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class CustomException extends RuntimeException {

  private final HttpStatus httpStatus; // Http 상태값
  private final int errorCode;  // 에러 코드
  private final String errorMessage;  // 에러 메시지

  public CustomException(ErrorCode errorCode) {

    //부모 클래스(RuntimeException)에 메시지를 전달
    super(errorCode.getMessage());
    this.httpStatus = errorCode.getHttpStatus();
    this.errorCode = errorCode.getCode();
    this.errorMessage = errorCode.getMessage();
  }

  // 동적 메시지를 보내기 위한 생성자
  public CustomException(ErrorCode errorCode, Object... args) {

    //부모 클래스(RuntimeException)에 메시지를 전달
    super(String.format(errorCode.getMessage(), args));
    this.httpStatus = errorCode.getHttpStatus();
    this.errorCode = errorCode.getCode();
    this.errorMessage = String.format(errorCode.getMessage(), args);
  }
}