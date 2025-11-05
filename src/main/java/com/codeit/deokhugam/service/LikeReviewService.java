package com.codeit.deokhugam.service;

public interface LikeReviewService {

  LikeReviewResult likeReview(LikeReviewCommand command);

  LikeReviewResult likeReviewV2(LikeReviewCommand command);

}
