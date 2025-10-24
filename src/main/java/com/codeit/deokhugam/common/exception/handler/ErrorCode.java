package com.codeit.deokhugam.common.exception.handler;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorCode {
  // User 관련 에러 코드
  USER_NOT_FOUND(404, "사용자 정보 없음", HttpStatus.NOT_FOUND),
  DUPLICATE_USER(409, "이미 존재하는 사용자입니다.", HttpStatus.CONFLICT),
  INVALID_USER_CREDENTIALS(401, "잘못된 사용자 인증 정보입니다.", HttpStatus.UNAUTHORIZED);

  private final int code;         // 에러 코드
  private final String message;      // 에러 메시지
  private final HttpStatus httpStatus; // HTTP 상태 코드
}