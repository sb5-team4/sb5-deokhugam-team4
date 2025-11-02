package com.codeit.deokhugam.dto.result;

import com.codeit.deokhugam.domain.entity.Notification;
import com.codeit.deokhugam.domain.entity.Review;
import java.time.Instant;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@AllArgsConstructor
@Getter
@Builder
public class GetNotificationOneResult {

  private final Long id;
  private final Long userId;
  private final Long reviewId;
  private final String reviewTitle;
  private final String content; // EX) [누굴까]님이 나의 리뷰를 좋아합니다.",
  private final Boolean confirmed;
  private final Instant createdAt;
  private final Instant updatedAt;

  public static GetNotificationOneResult from(
      Long userId,
      Review review,
      Notification notification
  ) {
    return GetNotificationOneResult.builder()
        .id(notification.getId())
        .userId(userId)
        .reviewId(review.getId())
        .reviewTitle(review.getContent())
        .content(notification.getContent())
        .confirmed(notification.isConfirmed())
        .createdAt(notification.getCreatedAt())
        .updatedAt(notification.getUpdatedAt())
        .build();
  }

}
