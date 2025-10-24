package com.codeit.deokhugam.common.exception.handler;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(staticName = "of")
public class ErrorResponse {

  private final int errorCode;
  private final String errorMessage;
}