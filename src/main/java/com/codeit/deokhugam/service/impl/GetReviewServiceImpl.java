package com.codeit.deokhugam.service.impl;

import com.codeit.deokhugam.common.exception.handler.CustomException;
import com.codeit.deokhugam.common.exception.handler.ErrorCode;
import com.codeit.deokhugam.domain.entity.Book;
import com.codeit.deokhugam.domain.entity.Member;
import com.codeit.deokhugam.domain.entity.PopularReview;
import com.codeit.deokhugam.domain.entity.Review;
import com.codeit.deokhugam.domain.enums.Period;
import com.codeit.deokhugam.dto.result.GetPopularReviewsResult;
import com.codeit.deokhugam.dto.result.GetReviewOneResult;
import com.codeit.deokhugam.dto.result.PaginatedResult;
import com.codeit.deokhugam.dto.result.PopularReviewResult;
import com.codeit.deokhugam.repository.BookRepository;
import com.codeit.deokhugam.repository.MemberRepository;
import com.codeit.deokhugam.repository.PopularReviewRepository;
import com.codeit.deokhugam.repository.ReviewLikeRepository;
import com.codeit.deokhugam.repository.ReviewRepository;
import com.codeit.deokhugam.service.GetPopularReviewsCommand;
import com.codeit.deokhugam.service.GetReviewService;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class GetReviewServiceImpl implements GetReviewService {

  private final ReviewRepository reviewRepository;
  private final BookRepository bookRepository;
  private final MemberRepository memberRepository;
  private final ReviewLikeRepository reviewLikeRepository;
  private final PopularReviewRepository popularReviewRepository;

  @Override
  @Transactional(readOnly = true)
  public GetReviewOneResult getReviewOne(Long id, Long memberId) {

    Review review = reviewRepository.findByIdAndDeletedIsFalse(id)
        .orElseThrow(() -> new CustomException(ErrorCode.REVIEW_NOT_FOUND));

    Book book = bookRepository.findByIdAndDeletedIsFalse(review.getBook().getId())
        .orElseThrow(() -> new CustomException(ErrorCode.BOOK_NOT_FOUND));

    Member member = memberRepository.findByIdAndDeletedIsFalse(review.getMember().getId())
        .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

    boolean likedByMe = reviewLikeRepository.existsReviewLikeByMemberIdAndReviewId(memberId, id);

    return GetReviewOneResult.builder()
        .id(id)
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
        .createdAt(Optional.ofNullable(review.getCreatedAt())
            .map(i -> OffsetDateTime.ofInstant(i, ZoneId.of("Asia/Seoul")))
            .orElse(null))
        .updatedAt(Optional.ofNullable(review.getUpdatedAt())
            .map(i -> OffsetDateTime.ofInstant(i, ZoneId.of("Asia/Seoul")))
            .orElse(null))
        .build();
  }

  @Override
  public GetPopularReviewsResult getPopularReviews(GetPopularReviewsCommand command) {

    Period period = command.getPeriod();
    Direction direction = command.getDirection();
    int limit = command.getLimit();
    Long cursor = command.getCursor(); // Nullable
    Instant after = command.getAfter(); // Nullable

    // 1. QueryDSL 결과
    PaginatedResult<PopularReview, Long> entitiesResult = popularReviewRepository
        .searchWithCursor(period, direction, limit, cursor, after);

    // 2. 필요한 Result 파싱
    List<PopularReviewResult> popularReviewResultsWithDetail = entitiesResult.getContent().stream()
        .map(pr -> {
              Review review = pr.getReview();
              Book book = pr.getReview().getBook();
              Member member = pr.getReview().getMember();

              return PopularReviewResult
                  .builder()
                  .id(pr.getId())
                  .reviewId(pr.getReview().getId())
                  .bookId(book.getId())
                  .bookTitle(book.getTitle())
                  .bookThumbnailUrl(book.getThumbnailUrl())
                  .userId(member.getId())
                  .userNickname(member.getNickname())
                  .reviewContent(review.getContent())
                  .reviewRating(review.getRating())
                  .period(Period.from(pr.getPeriod()))
                  .createdAt(pr.getCreatedAt())
                  .rank(pr.getRank())
                  .score(pr.getScore())
                  .likeCount(review.getLikeCount())
                  .commentCount(review.getCommentCount())
                  .build();
            }
        ).toList();

    // 3. 결과 리턴
    return GetPopularReviewsResult.builder()
        .popularReviews(popularReviewResultsWithDetail)
        .nextCursor(entitiesResult.getNextCursor())
        .nextAfter(entitiesResult.getNextAfter())
        .size(entitiesResult.getSize())
        .totalElements(entitiesResult.getTotalElements())
        .hasNext(entitiesResult.getHasNext())
        .build();
  }
}
