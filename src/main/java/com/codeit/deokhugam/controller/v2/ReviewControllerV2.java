package com.codeit.deokhugam.controller.v2;

import com.codeit.deokhugam.dto.response.LikeReviewResponse;
import com.codeit.deokhugam.mapper.likeReview.LikeReviewMapper;
import com.codeit.deokhugam.service.LikeReviewCommand;
import com.codeit.deokhugam.service.LikeReviewResult;
import com.codeit.deokhugam.service.LikeReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v2/reviews")
public class ReviewControllerV2 {

  private final LikeReviewService likeReviewService;
  private final LikeReviewMapper likeReviewMapper;


  @PostMapping("/{reviewId}/like")
  public ResponseEntity<LikeReviewResponse> likeReview(
      @PathVariable Long reviewId,
      @RequestHeader("Deokhugam-Request-User-ID") Long memberId
  ) {
    LikeReviewResult result = likeReviewService.likeReviewV2(LikeReviewCommand.builder()
        .reviewId(reviewId)
        .memberId(memberId)
        .build());

    return ResponseEntity.ok(likeReviewMapper.toResponse(result));
  }
}
