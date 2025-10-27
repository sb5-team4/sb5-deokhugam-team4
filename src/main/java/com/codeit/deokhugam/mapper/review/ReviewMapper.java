package com.codeit.deokhugam.mapper.review;

import com.codeit.deokhugam.dto.command.CreateReviewCommand;
import com.codeit.deokhugam.dto.request.review.CreateReviewRequest;
import com.codeit.deokhugam.dto.response.review.ReviewResponse;
import com.codeit.deokhugam.dto.result.CreateReviewResult;
import com.codeit.deokhugam.dto.result.PatchReviewResult;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ReviewMapper {

  CreateReviewCommand toCommand(CreateReviewRequest request);


  ReviewResponse toResponse(CreateReviewResult result);

  ReviewResponse toResponse(PatchReviewResult result);


}
