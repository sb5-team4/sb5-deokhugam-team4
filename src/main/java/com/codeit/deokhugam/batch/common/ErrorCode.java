package com.codeit.deokhugam.batch.common;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorCode {

  // PopularReview Batch 관련 에러
  PROCESS_START_TIME_NOT_LOAD(1001, "JOB 시작 시간이 ItemProcessor에 로드되지 않았습니다.");

  private final int code;         // 에러 코드
  private final String message;      // 에러 메시지


}
