package com.codeit.deokhugam.controller.review;

import com.codeit.deokhugam.dto.request.review.CreateReviewRequest;
import com.codeit.deokhugam.dto.response.review.ReviewResponse;
import com.codeit.deokhugam.dto.result.CreateReviewResult;
import com.codeit.deokhugam.mapper.review.ReviewMapper;
import com.codeit.deokhugam.service.ReviewService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/reviews")
public class ReviewController {

  private final ReviewService reviewService;
  private final ReviewMapper reviewMapper;

  @PostMapping
  public ResponseEntity<ReviewResponse> createReview(
      @Valid @RequestBody CreateReviewRequest request) {

    CreateReviewResult result = reviewService.createReview(reviewMapper.toCommand(request));
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(reviewMapper.toResponse(result));

  }


}
