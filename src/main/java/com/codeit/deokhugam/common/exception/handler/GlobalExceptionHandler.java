package com.codeit.deokhugam.common.exception.handler;

import com.codeit.deokhugam.common.exception.AuthorizationException;
import com.codeit.deokhugam.common.exception.ResourceNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

  // CommentCreateRequest의 @NotBlank, @NotNull 등이 유효성 검사 실패 시 (400 Bad Request)
  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ErrorResponse> handleValidationExceptions(
      MethodArgumentNotValidException ex) {
    String errorMessage = ex.getBindingResult().getAllErrors().get(0).getDefaultMessage();
    ErrorResponse response = new ErrorResponse(HttpStatus.BAD_REQUEST.value(), errorMessage);
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
  }

  @ExceptionHandler(MissingRequestHeaderException.class) // todo 검증해야함
  public ResponseEntity<ErrorResponse> handleValidationExceptions(
      MissingRequestHeaderException ex) {
    String headerName = ex.getHeaderName();
    String message = headerName + " 헤더가 요청에 포함되어야 합니다.";
    ErrorResponse response = new ErrorResponse(HttpStatus.BAD_REQUEST.value(), message);
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
  }

  // Service에서 Review나 Member를 찾지 못했을 때
  // 커스텀예외 ResourceNotFoundException 처리 (404 Not Found)
  @ExceptionHandler(ResourceNotFoundException.class)
  public ResponseEntity<ErrorResponse> handleResourceNotFound(ResourceNotFoundException ex) {
    ErrorResponse response = new ErrorResponse(HttpStatus.NOT_FOUND.value(), ex.getMessage());
    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
  }

  // Controller에서 헤더 ID와 바디 ID가 다를 때
  // 커스텀예외 AuthorizationException 처리 (403 Forbidden)
  @ExceptionHandler(AuthorizationException.class)
  public ResponseEntity<ErrorResponse> handlePermissionDenied(AuthorizationException ex) {
    ErrorResponse response = new ErrorResponse(HttpStatus.FORBIDDEN.value(), ex.getMessage());
    return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
  }


  //처리하지 못한 모든 예외 처리 (500 Internal Server Error)
  @ExceptionHandler(Exception.class)
  public ResponseEntity<ErrorResponse> handleGlobalException(Exception ex) {
    ErrorResponse response = new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(),
        "서버 내부 오류가 발생했습니다.");
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
  }


  // 공통 에러 응답 DTO (API 명세서의 에러 응답 형태)
  public record ErrorResponse(int code, String message) {

  }
}