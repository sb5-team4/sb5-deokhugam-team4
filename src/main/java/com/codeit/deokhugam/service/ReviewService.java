package com.codeit.deokhugam.service;

import com.codeit.deokhugam.dto.command.CreateReviewCommand;
import com.codeit.deokhugam.dto.command.HardDeleteReviewCommand;
import com.codeit.deokhugam.dto.command.PatchReviewCommand;
import com.codeit.deokhugam.dto.command.SoftDeleteReviewCommand;
import com.codeit.deokhugam.dto.result.CreateReviewResult;
import com.codeit.deokhugam.dto.result.PatchReviewResult;

public interface ReviewService {

  CreateReviewResult createReview(CreateReviewCommand createReviewCommand);

  boolean softDelete(SoftDeleteReviewCommand command);

  boolean hardDelete(HardDeleteReviewCommand command);

  PatchReviewResult patchReview(PatchReviewCommand command);
}
