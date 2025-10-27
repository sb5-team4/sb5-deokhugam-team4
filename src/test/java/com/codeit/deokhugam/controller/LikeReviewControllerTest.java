package com.codeit.deokhugam.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.codeit.deokhugam.common.exception.ResourceNotFoundException;
import com.codeit.deokhugam.common.exception.handler.GlobalExceptionHandler;
import com.codeit.deokhugam.controller.review.ReviewController;
import com.codeit.deokhugam.dto.response.LikeReviewResponse;
import com.codeit.deokhugam.mapper.likeReview.LikeReviewMapper;
import com.codeit.deokhugam.mapper.review.ReviewMapper;
import com.codeit.deokhugam.service.GetReviewService;
import com.codeit.deokhugam.service.LikeReviewResult;
import com.codeit.deokhugam.service.LikeReviewService;
import com.codeit.deokhugam.service.ReviewService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(ReviewController.class)
@Import(GlobalExceptionHandler.class)
public class LikeReviewControllerTest {

  @Autowired
  MockMvc mockMvc;
  @Autowired
  ObjectMapper objectMapper;

  @MockitoBean
  ReviewService reviewService;
  @MockitoBean
  LikeReviewService likeReviewService;
  @MockitoBean
  ReviewMapper reviewMapper;
  @MockitoBean
  LikeReviewMapper likeReviewMapper;
  @MockitoBean
  GetReviewService getReviewService;
  @MockitoBean
  JpaMetamodelMappingContext jpaMetamodelMappingContext;

  long reviewId;
  long memberId;
  boolean liked;
  LikeReviewResult result;
  LikeReviewResponse response;

  @BeforeEach
  void setUp() {
    reviewId = 1L;
    memberId = 1L;
    liked = true;
    result = LikeReviewResult.builder()
        .reviewId(reviewId)
        .memberId(memberId)
        .liked(liked)
        .build();

    response = LikeReviewResponse.builder()
        .reviewId(reviewId)
        .memberId(memberId)
        .liked(liked)
        .build();

  }

  @Test
  @DisplayName("post - 리뷰 좋아요 테스트")
  public void hardDeleteReview() throws Exception {

    Long reviewId = 1L;
    Long memberId = 1L;

    given(likeReviewService.likeReview(any())).willReturn(result);
    given(likeReviewMapper.toResponse(any())).willReturn(response);

    mockMvc.perform(post("/api/reviews/" + reviewId + "/like")
            .param("id", String.valueOf(reviewId))
            .header("Deokhugam-Request-User-ID", String.valueOf(memberId))
        )
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.reviewId").value(reviewId)) // 숫자 비교
        .andExpect(jsonPath("$.memberId").value(memberId)) // 숫자 비교
        .andExpect(jsonPath("$.liked").value(liked)); // boolean 비교
  }


  @Test
  @DisplayName("post - 리뷰 좋아요 테스트 - 잘못된 요청 (400 에러)")
  public void hardDeleteReviewWithBadRequest() throws Exception {

    long reviewId = 1L;
    mockMvc.perform(post("/api/reviews/" + reviewId + "/like")
        )
        .andExpect(status().isBadRequest());

  }

  @Test
  @DisplayName("post - 리뷰 좋아요 테스트 - 리뷰 없음 (404 에러)")
  public void hardDeleteReviewWithNotFound() throws Exception {
    Long reviewId = 1L;
    Long MemberId = 1L;
    given(likeReviewService.likeReview(any())).willThrow(ResourceNotFoundException.class);

    mockMvc.perform(post("/api/reviews/" + reviewId + "/like")
            .param("id", String.valueOf(reviewId))
            .header("Deokhugam-Request-User-ID", String.valueOf(MemberId))
        )
        .andExpect(status().isNotFound());
  }

  @Test
  @DisplayName("post - 리뷰 좋아요 테스트 - 예상치 못한 에러 (500 에러)")
  public void hardDeleteReviewWithInternalServerError() throws Exception {

    Long reviewId = 1L;
    Long MemberId = 1L;
    given(likeReviewService.likeReview(any())).willThrow(RuntimeException.class);

    mockMvc.perform(post("/api/reviews/" + reviewId + "/like")
            .param("id", String.valueOf(reviewId))
            .header("Deokhugam-Request-User-ID", String.valueOf(MemberId))
        )
        .andExpect(status().isInternalServerError());
  }
}
