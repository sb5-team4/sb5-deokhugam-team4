package com.codeit.deokhugam.exception.handler;

import java.time.Instant;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ErrorResponse {

  private Instant timestamp;
  private Integer status;
  private String message;
  private String detail;
}
