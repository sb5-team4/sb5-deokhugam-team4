package com.codeit.deokhugam.exception.handler;

import java.time.Instant;
import java.util.NoSuchElementException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(NoSuchElementException.class)
  public ResponseEntity<ErrorResponse> handleException(NoSuchElementException e) {
    return new ResponseEntity<>(ErrorResponse.builder()
        .timestamp(Instant.now())
        .status(HttpStatus.NOT_FOUND.value())
        .message(e.getMessage())
        .detail("")
        .build(), HttpStatus.NOT_FOUND);
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<ErrorResponse> handleException(Exception e) {
    return new ResponseEntity<>(ErrorResponse.builder()
        .timestamp(Instant.now())
        .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
        .message(e.getMessage())
        .detail("")
        .build(), HttpStatus.INTERNAL_SERVER_ERROR);
  }


}
