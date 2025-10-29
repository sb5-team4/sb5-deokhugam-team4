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
  DELETED_BOOK_CANNOT_BE_MODIFIED(400, "삭제된 도서는 수정할 수 없습니다.", HttpStatus.BAD_REQUEST),
  DUPLICATE_ISBN(409, "이미 존재하는 ISBN입니다.", HttpStatus.CONFLICT),
  BOOK_ISBN_NOT_FOUND(404, "해당 ISBN의 도서 정보를 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
  ISBN_NOT_COLLECT(400, "ISBN은 13자리 숫자여야 합니다.", HttpStatus.BAD_REQUEST),

  // File 관련 에러 코드
  INVALID_FILE(400, "유효하지 않은 파일입니다.", HttpStatus.BAD_REQUEST),
  FILE_UPLOAD_FAILED(500, "파일 업로드에 실패했습니다.", HttpStatus.INTERNAL_SERVER_ERROR);

  private final int code;         // 에러 코드
  private final String message;      // 에러 메시지
  private final HttpStatus httpStatus; // HTTP 상태 코드
}