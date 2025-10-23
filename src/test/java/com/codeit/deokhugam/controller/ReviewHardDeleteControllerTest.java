package com.codeit.deokhugam.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.codeit.deokhugam.common.exception.AuthorizationException;
import com.codeit.deokhugam.common.exception.ResourceNotFoundException;
import com.codeit.deokhugam.common.exception.handler.GlobalExceptionHandler;
import com.codeit.deokhugam.controller.review.ReviewController;
import com.codeit.deokhugam.mapper.review.ReviewMapper;
import com.codeit.deokhugam.service.ReviewService;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.OffsetDateTime;
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
public class ReviewHardDeleteControllerTest {

  @Autowired
  MockMvc mockMvc;
  @Autowired
  ObjectMapper objectMapper;

  @MockitoBean
  ReviewService reviewService;
  @MockitoBean
  ReviewMapper reviewMapper;
  @MockitoBean
  JpaMetamodelMappingContext jpaMetamodelMappingContext;

  Long bookId;
  Long userId;
  String content;
  short rating;

  Long id;
  String bookTitle;
  String bookThumbnailUrl;
  String userNickname;
  Long likeCount;
  Long commentCount;
  boolean likedByMe;
  OffsetDateTime createdAt;
  OffsetDateTime updatedAt;

  @BeforeEach
  void setUp() {
    bookId = 1L;
    userId = 1L;
    content = "This is a test review";
    rating = (short) 1;
    id = 1L;
    bookTitle = "This is a test book";
    bookThumbnailUrl = "This is a test book thumbnail";
    userNickname = "this is a test user";
    likeCount = 0L;
    commentCount = 0L;
    likedByMe = false;
    createdAt = OffsetDateTime.now();
    updatedAt = OffsetDateTime.now();

  }

  @Test
  @DisplayName("delete - 리뷰 물리 삭제 테스트")
  public void hardDeleteReview() throws Exception {

    Long reviewId = 1L;
    Long MemberId = 1L;

    given(reviewService.hardDelete(any())).willReturn(true);

    mockMvc.perform(delete("/api/reviews/" + reviewId + "/hard")
            .param("id", String.valueOf(reviewId))
            .header("Deokhugam-Request-User-ID", String.valueOf(MemberId))
        )
        .andExpect(status().isNoContent());
  }


  @Test
  @DisplayName("delete - 리뷰 물리 삭제 테스트 - 잘못된 요청 (400 에러)")
  public void hardDeleteReviewWithBadRequest() throws Exception {

    long reviewId = 1L;
    mockMvc.perform(delete("/api/reviews/" + reviewId + "/hard")
        )
        .andExpect(status().isBadRequest());

  }

  @Test
  @DisplayName("delete - 리뷰 물리 삭제 테스트 - 권한없음 (403 에러)")
  public void hardDeleteReviewWithNotAllowed() throws Exception {

    Long reviewId = 1L;
    Long MemberId = 1L;
    given(reviewService.hardDelete(any())).willThrow(AuthorizationException.class);

    mockMvc.perform(delete("/api/reviews/" + reviewId + "/hard")
            .param("id", String.valueOf(reviewId))
            .header("Deokhugam-Request-User-ID", String.valueOf(MemberId))
        )
        .andExpect(status().isForbidden());
  }

  @Test
  @DisplayName("delete - 리뷰 물리 삭제 테스트 - 리뷰 없음 (404 에러)")
  public void hardDeleteReviewWithNotFound() throws Exception {
    Long reviewId = 1L;
    Long MemberId = 1L;
    given(reviewService.hardDelete(any())).willThrow(ResourceNotFoundException.class);

    mockMvc.perform(delete("/api/reviews/" + reviewId + "/hard")
            .param("id", String.valueOf(reviewId))
            .header("Deokhugam-Request-User-ID", String.valueOf(MemberId))
        )
        .andExpect(status().isNotFound());
  }

  @Test
  @DisplayName("delete - 리뷰 물리 삭제 테스트 - 예상치 못한 에러 (500 에러)")
  public void hardDeleteReviewWithInternalServerError() throws Exception {

    Long reviewId = 1L;
    Long MemberId = 1L;
    given(reviewService.hardDelete(any())).willThrow(RuntimeException.class);

    mockMvc.perform(delete("/api/reviews/" + reviewId + "/hard")
            .param("id", String.valueOf(reviewId))
            .header("Deokhugam-Request-User-ID", String.valueOf(MemberId))
        )
        .andExpect(status().isInternalServerError());
  }


}
