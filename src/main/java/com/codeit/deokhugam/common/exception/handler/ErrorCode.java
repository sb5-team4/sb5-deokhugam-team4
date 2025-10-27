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
  INVALID_USER_CREDENTIALS(401, "잘못된 사용자 인증 정보입니다.", HttpStatus.UNAUTHORIZED),
  USER_NOT_AUTHORIZED(403, "사용자 삭제 권한 없음", HttpStatus.FORBIDDEN),

  // Book 관련 에러 코드
  BOOK_NOT_FOUND(404, "해당하는 도서 ID가 존재하지 않습니다.", HttpStatus.NOT_FOUND),
  BOOK_ALREADY_DELETED(400, "이미 삭제된 도서입니다.", HttpStatus.BAD_REQUEST),
  DELETED_BOOK_CANNOT_BE_MODIFIED(400, "삭제된 도서는 수정할 수 없습니다.", HttpStatus.BAD_REQUEST);

  private final int code;         // 에러 코드
  private final String message;      // 에러 메시지
  private final HttpStatus httpStatus; // HTTP 상태 코드
}