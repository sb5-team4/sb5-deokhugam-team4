package com.codeit.deokhugam.service;

import com.codeit.deokhugam.dto.result.GetPopularReviewsResult;
import com.codeit.deokhugam.dto.result.GetReviewOneResult;

public interface GetReviewService {


  GetReviewOneResult getReviewOne(Long id, Long memberId);

  GetPopularReviewsResult getPopularReviews(GetPopularReviewsCommand command);

}
