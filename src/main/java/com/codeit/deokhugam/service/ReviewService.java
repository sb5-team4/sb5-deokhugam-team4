package com.codeit.deokhugam.service;

import com.codeit.deokhugam.dto.command.CreateReviewCommand;
import com.codeit.deokhugam.dto.result.CreateReviewResult;

public interface ReviewService {

  CreateReviewResult createReview(CreateReviewCommand createReviewCommand);
}
