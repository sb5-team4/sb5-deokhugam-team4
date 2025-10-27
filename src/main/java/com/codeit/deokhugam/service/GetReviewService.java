package com.codeit.deokhugam.service;

import com.codeit.deokhugam.dto.result.GetReviewOneResult;

public interface GetReviewService {


  GetReviewOneResult getReviewOne(Long id, Long memberId);

}
