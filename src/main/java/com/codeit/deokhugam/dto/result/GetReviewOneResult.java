package com.codeit.deokhugam.dto.result;

import com.codeit.deokhugam.domain.entity.Book;
import com.codeit.deokhugam.domain.entity.Member;
import com.codeit.deokhugam.domain.entity.Review;
import java.time.Instant;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@AllArgsConstructor
@Getter
@Builder
public class GetReviewOneResult {

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

  public static GetReviewOneResult from(
      Review review,
      Book book,
      Member member,
      boolean likedByMe
  ) {
    return GetReviewOneResult.builder()
        .id(review.getId())
        .bookId(book.getId())
        .bookTitle(book.getTitle())
        .bookThumbnailUrl(book.getThumbnailUrl())
        .userId(member.getId())
        .userNickname(member.getNickname())
        .content(review.getContent())
        .rating(review.getRating())
        .likeCount(review.getLikeCount())
        .commentCount(review.getCommentCount())
        .likedByMe(likedByMe)
        .createdAt(review.getCreatedAt())
        .updatedAt(review.getUpdatedAt())
        .build();

  }
}
