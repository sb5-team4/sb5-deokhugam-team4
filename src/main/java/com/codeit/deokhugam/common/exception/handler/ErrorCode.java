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
  FILE_UPLOAD_FAILED(500, "파일 업로드에 실패했습니다.", HttpStatus.INTERNAL_SERVER_ERROR),

  // Review 관련 에러 코드
  REVIEW_NOT_FOUND(404, "해당하는 리뷰 ID가 존재하지 않습니다.", HttpStatus.NOT_FOUND),

  // Notification 관련 에러 코드
  NOTIFICATION_NOT_AUTHORIZED(403, "알림 수정 권한 없음", HttpStatus.FORBIDDEN),
  NOTIFICATION_NOT_FOUND(404, "해당하는 알림 ID가 존재하지 않습니다.", HttpStatus.NOT_FOUND),
  NOTIFICATION_SEND_FAILED(1001, "알림 전송 실패", null),
  
  // Comment 관련 에러 코드
  // 400 BAD_REQUEST: 잘못된 요청 (파라미터 누락, 타입 불일치 등)
  INVALID_REQUEST_PARAMETERS(400, "요청 파라미터가 잘못되었습니다.", HttpStatus.BAD_REQUEST),

  // 403 FORBIDDEN: 권한 없음 (댓글 생성/수정/삭제 시 사용)
  COMMENT_NOT_AUTHORIZED(403, "댓글에 대한 권한이 없습니다.", HttpStatus.FORBIDDEN),

  // 404 NOT_FOUND: 리소스 없음
  COMMENT_REVIEW_NOT_FOUND(404, "해당 리뷰를 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
  COMMENT_NOT_FOUND(404, "해당 댓글을 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
  COMMENT_USER_NOT_FOUND(404, "해당 사용자를 찾을 수 없습니다.", HttpStatus.NOT_FOUND);

  private final int code;         // 에러 코드
  private final String message;      // 에러 메시지
  private final HttpStatus httpStatus; // HTTP 상태 코드
}