package com.codeit.deokhugam.dto.result;


import java.time.Instant;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@AllArgsConstructor
@Getter
@Builder
public class ReadNotificationResult {

  private final Long id;
  private final Long userId;
  private final Long reviewId;
  private final String reviewTitle;
  private final String content; // EX) [누굴까]님이 나의 리뷰를 좋아합니다.",
  private final Boolean confirmed;
  private final Instant createdAt;
  private final Instant updatedAt;
}
