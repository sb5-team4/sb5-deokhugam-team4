package com.codeit.deokhugam.controller.review;

import com.codeit.deokhugam.domain.enums.Period;
import com.codeit.deokhugam.dto.command.HardDeleteReviewCommand;
import com.codeit.deokhugam.dto.command.PatchReviewCommand;
import com.codeit.deokhugam.dto.command.SoftDeleteReviewCommand;
import com.codeit.deokhugam.dto.request.PatchReviewRequest;
import com.codeit.deokhugam.dto.request.review.CreateReviewRequest;
import com.codeit.deokhugam.dto.response.CursorPageResponse;
import com.codeit.deokhugam.dto.response.LikeReviewResponse;
import com.codeit.deokhugam.dto.response.PopularReviewResponse;
import com.codeit.deokhugam.dto.response.review.ReviewResponse;
import com.codeit.deokhugam.dto.result.CreateReviewResult;
import com.codeit.deokhugam.dto.result.GetPopularReviewsResult;
import com.codeit.deokhugam.dto.result.GetReviewOneResult;
import com.codeit.deokhugam.dto.result.PatchReviewResult;
import com.codeit.deokhugam.mapper.likeReview.LikeReviewMapper;
import com.codeit.deokhugam.mapper.review.ReviewMapper;
import com.codeit.deokhugam.service.GetPopularReviewsCommand;
import com.codeit.deokhugam.service.GetReviewService;
import com.codeit.deokhugam.service.LikeReviewCommand;
import com.codeit.deokhugam.service.LikeReviewResult;
import com.codeit.deokhugam.service.LikeReviewService;
import com.codeit.deokhugam.service.ReviewService;
import jakarta.validation.Valid;
import java.time.Instant;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/reviews")
public class ReviewController {

  private final ReviewService reviewService;
  private final GetReviewService getReviewService;
  private final ReviewMapper reviewMapper;
  private final LikeReviewService likeReviewService;
  private final LikeReviewMapper likeReviewMapper;

  @PostMapping
  public ResponseEntity<ReviewResponse> createReview(
      @Valid @RequestBody CreateReviewRequest request) {

    CreateReviewResult result = reviewService.createReview(reviewMapper.toCommand(request));
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(reviewMapper.toResponse(result));

  }

  @DeleteMapping("/{reviewId}")
  public ResponseEntity<Void> deleteReview(
      @PathVariable Long reviewId,
      @RequestHeader("Deokhugam-Request-User-ID") Long memberId
  ) {

    reviewService.softDelete(SoftDeleteReviewCommand.builder()
        .reviewId(reviewId)
        .memberId(memberId)
        .build());

    return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
  }

  @DeleteMapping("/{reviewId}/hard")
  public ResponseEntity<Void> hardDeleteReview(
      @PathVariable Long reviewId,
      @RequestHeader("Deokhugam-Request-User-ID") Long memberId
  ) {

    reviewService.hardDelete(HardDeleteReviewCommand.builder()
        .reviewId(reviewId)
        .memberId(memberId)
        .build());

    return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
  }

  @PostMapping("/{reviewId}/like")
  public ResponseEntity<LikeReviewResponse> likeReview(
      @PathVariable Long reviewId,
      @RequestHeader("Deokhugam-Request-User-ID") Long memberId
  ) {
    LikeReviewResult result = likeReviewService.likeReview(LikeReviewCommand.builder()
        .reviewId(reviewId)
        .memberId(memberId)
        .build());

    return ResponseEntity.ok(likeReviewMapper.toResponse(result));
  }

  @PatchMapping("/{reviewId}")
  public ResponseEntity<ReviewResponse> patchReview(
      @PathVariable Long reviewId,
      @RequestHeader("Deokhugam-Request-User-ID") Long memberId,
      @Valid @RequestBody PatchReviewRequest request
  ) {

    PatchReviewResult result = reviewService.patchReview(PatchReviewCommand.builder()
        .memberId(memberId)
        .reviewId(reviewId)
        .newContent(request.getContent())
        .newRating(request.getRating())
        .build());

    return ResponseEntity.ok(reviewMapper.toResponse(result));
  }

  @GetMapping("/{reviewId}")
  public ResponseEntity<ReviewResponse> getReview(
      @PathVariable Long reviewId,
      @RequestHeader("Deokhugam-Request-User-ID") Long memberId
  ) {
    GetReviewOneResult result = getReviewService.getReviewOne(reviewId, memberId);

    return ResponseEntity.ok(reviewMapper.toResponse(result));
  }

  @GetMapping("/popular")
  public ResponseEntity<CursorPageResponse<PopularReviewResponse, Long>> getPopularReviews(
      @RequestParam(defaultValue = "DAILY") Period period,
      @RequestParam(defaultValue = "ASC") Direction direction,
      @RequestParam(required = false) Long cursor,
      @RequestParam(required = false) Instant after,
      @RequestParam(defaultValue = "50") Integer limit

  ) {

    GetPopularReviewsResult result = getReviewService.getPopularReviews(GetPopularReviewsCommand
        .builder()
        .period(period)
        .direction(direction)
        .cursor(cursor)
        .after(after)
        .limit(limit)
        .build());

    CursorPageResponse<PopularReviewResponse, Long> response = CursorPageResponse
        .<PopularReviewResponse, Long>builder()
        .content(result.getPopularReviews()
            .stream().map(reviewMapper::toResponse)
            .toList())
        .nextCursor(result.getNextCursor())
        .nextAfter(result.getNextAfter())
        .size((result.getSize()))
        .totalElements(result.getTotalElements())
        .hasNext(result.getHasNext())
        .build();
    return ResponseEntity.ok(response);
  }


}
