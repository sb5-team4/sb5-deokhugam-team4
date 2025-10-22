package com.codeit.deokhugam.common.exception;


// Controller에서 헤더 ID와 요청 바디의 userId가 일치하지 않는 등 권한이 없을 때
public class AuthorizationException extends RuntimeException {

  public AuthorizationException(String message) {
    super(message);
  }
}
