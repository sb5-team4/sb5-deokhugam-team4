package com.codeit.deokhugam.controller.review;

import com.codeit.deokhugam.dto.command.HardDeleteReviewCommand;
import com.codeit.deokhugam.dto.command.SoftDeleteReviewCommand;
import com.codeit.deokhugam.dto.request.review.CreateReviewRequest;
import com.codeit.deokhugam.dto.response.LikeReviewResponse;
import com.codeit.deokhugam.dto.response.review.ReviewResponse;
import com.codeit.deokhugam.dto.result.CreateReviewResult;
import com.codeit.deokhugam.mapper.likeReview.LikeReviewMapper;
import com.codeit.deokhugam.mapper.review.ReviewMapper;
import com.codeit.deokhugam.service.LikeReviewCommand;
import com.codeit.deokhugam.service.LikeReviewResult;
import com.codeit.deokhugam.service.LikeReviewService;
import com.codeit.deokhugam.service.ReviewService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/reviews")
public class ReviewController {

  private final ReviewService reviewService;
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


}
