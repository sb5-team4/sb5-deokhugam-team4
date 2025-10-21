package com.codeit.deokhugam.common.exception;


//Service에서 Review나 Member를 findById로 조회했을 때 데이터가 없을때 404 반환
public class ResourceNotFoundException extends RuntimeException {

  public ResourceNotFoundException(String message) {
    super(message);
  }

}
