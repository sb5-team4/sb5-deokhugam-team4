package com.codeit.deokhugam.dto.response.review;

import java.time.ZonedDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@AllArgsConstructor
@Getter
@Builder
public class ReviewResponse {

  private final Long id;
  private final Long bookId;
  private final String bookTitle;
  private final String bookThumbnailUrl;
  private final Long userId;
  private final String userNickname;
  private final String content;
  private final int rating;
  private final Long likeCount;
  private final Long commentCount;
  private final boolean likedByMe;
  private final ZonedDateTime createdAt;
  private final ZonedDateTime updatedAt;
}
