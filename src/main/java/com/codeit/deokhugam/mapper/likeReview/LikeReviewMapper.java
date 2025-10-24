package com.codeit.deokhugam.mapper.likeReview;

import com.codeit.deokhugam.dto.response.LikeReviewResponse;
import com.codeit.deokhugam.service.LikeReviewResult;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface LikeReviewMapper {

  LikeReviewResponse toResponse(LikeReviewResult result);

}
