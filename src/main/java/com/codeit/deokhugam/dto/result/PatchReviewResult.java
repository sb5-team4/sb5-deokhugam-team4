package com.codeit.deokhugam.dto.result;

import java.time.Instant;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@AllArgsConstructor
@Getter
@Builder
public class PatchReviewResult {

  private final Long id;
  private final Long bookId;
  private final String bookTitle;
  private final String bookThumbnailUrl;
  private final Long userId;
  private final String userNickname;
  private final String content;
  private final short rating;
  private final Long likeCount;
  private final Long commentCount;
  private final boolean likedByMe;
  private final Instant createdAt;
  private final Instant updatedAt;
}
