package com.codeit.deokhugam.service;

import com.codeit.deokhugam.dto.command.GetReviewsCommand;
import com.codeit.deokhugam.dto.result.GetPopularReviewsResult;
import com.codeit.deokhugam.dto.result.GetReviewOneResult;
import com.codeit.deokhugam.dto.result.GetReviewsResult;

public interface GetReviewService {


  GetReviewOneResult getReviewOne(Long id, Long memberId);

  GetPopularReviewsResult getPopularReviews(GetPopularReviewsCommand command);

  GetReviewsResult getReviews(GetReviewsCommand command);
}
