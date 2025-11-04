package com.codeit.deokhugam.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.codeit.deokhugam.common.exception.AuthorizationException;
import com.codeit.deokhugam.common.exception.ResourceNotFoundException;
import com.codeit.deokhugam.common.exception.handler.GlobalExceptionHandler;
import com.codeit.deokhugam.controller.review.ReviewController;
import com.codeit.deokhugam.dto.request.PatchReviewRequest;
import com.codeit.deokhugam.dto.response.review.ReviewResponse;
import com.codeit.deokhugam.dto.result.PatchReviewResult;
import com.codeit.deokhugam.mapper.likeReview.LikeReviewMapper;
import com.codeit.deokhugam.mapper.review.ReviewMapper;
import com.codeit.deokhugam.service.GetReviewService;
import com.codeit.deokhugam.service.LikeReviewService;
import com.codeit.deokhugam.service.ReviewService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

@WebMvcTest(ReviewController.class)
@Import(GlobalExceptionHandler.class)
public class PatchReviewControllerV2Test {

  @Autowired
  MockMvc mockMvc;
  @Autowired
  ObjectMapper objectMapper;

  @MockitoBean
  ReviewService reviewService;
  @MockitoBean
  ReviewMapper reviewMapper;
  @MockitoBean
  LikeReviewService likeReviewService;
  @MockitoBean
  LikeReviewMapper likeReviewMapper;
  @MockitoBean
  GetReviewService getReviewService;
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
  Instant createdAt;
  Instant updatedAt;

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
    createdAt = Instant.now();
    updatedAt = Instant.now();

  }

  @Test
  @DisplayName("patch - 리뷰 수정 테스트")
  public void patchReview() throws Exception {
    String newContent = "This is a test review";
    short newRating = (short) 5;

    PatchReviewResult result = PatchReviewResult.builder()
        .id(id)
        .bookId(bookId)
        .bookTitle(bookTitle)
        .bookThumbnailUrl(bookThumbnailUrl)
        .userId(userId)
        .userNickname(userNickname)
        .content(newContent) // 두 필드만 변화
        .rating(newRating) // 두 필드만 변화
        .likeCount(likeCount)
        .commentCount(commentCount)
        .likedByMe(likedByMe)
        .createdAt(createdAt)
        .updatedAt(updatedAt)
        .build();

    PatchReviewRequest request = new PatchReviewRequest(newContent, newRating);

    ReviewResponse response = ReviewResponse.builder()
        .id(id)
        .bookId(bookId)
        .bookTitle(bookTitle)
        .bookThumbnailUrl(bookThumbnailUrl)
        .userId(userId)
        .userNickname(userNickname)
        .content(newContent)
        .rating(newRating)
        .likeCount(likeCount)
        .commentCount(commentCount)
        .likedByMe(likedByMe)
        .createdAt(createdAt)
        .updatedAt(updatedAt)
        .build();

    Long reviewId = 1L;
    Long MemberId = 1L;

    given(reviewService.patchReview(any())).willReturn(result);
    given(reviewMapper.toResponse(any(PatchReviewResult.class))).willReturn(response);

    MvcResult requestResult = mockMvc.perform(patch("/api/reviews/" + reviewId)
            .param("id", String.valueOf(reviewId))
            .header("Deokhugam-Request-User-ID", String.valueOf(MemberId))
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request))
        )
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(id))
        .andExpect(jsonPath("$.bookId").value(bookId))
        .andExpect(jsonPath("$.bookTitle").value(bookTitle))
        .andExpect(jsonPath("$.bookThumbnailUrl").value(bookThumbnailUrl))
        .andExpect(jsonPath("$.userId").value(userId))
        .andExpect(jsonPath("$.userNickname").value(userNickname))
        .andExpect(jsonPath("$.content").value(newContent))
        .andExpect(jsonPath("$.rating").value("" + newRating))
        .andExpect(jsonPath("$.likeCount").value(likeCount))
        .andExpect(jsonPath("$.commentCount").value(commentCount))
        .andExpect(jsonPath("$.likedByMe").value(likedByMe)).andReturn();

    String responseBody = requestResult.getResponse().getContentAsString();

// JSON 에서 createdAt, updatedAt 추출
    Instant actualCreatedAt = Instant.parse(
        JsonPath.read(responseBody, "$.createdAt"));
    Instant actualUpdatedAt = Instant.parse(
        JsonPath.read(responseBody, "$.updatedAt"));

    assertEquals(createdAt.truncatedTo(ChronoUnit.MILLIS),
        actualCreatedAt.truncatedTo(ChronoUnit.MILLIS));
    assertEquals(updatedAt.truncatedTo(ChronoUnit.MILLIS),
        actualUpdatedAt.truncatedTo(ChronoUnit.MILLIS));
  }

  @Test
  @DisplayName("patch - 리뷰 수정 테스트 - 403 응답 확인")
  public void patchReviewWithNotAllowedException() throws Exception {

    String newContent = "This is a test review";
    short newRating = (short) 5;
    PatchReviewRequest request = new PatchReviewRequest(newContent, newRating);

    Long reviewId = 1L;
    Long MemberId = 1L;

    given(reviewService.patchReview(any())).willThrow(AuthorizationException.class);

    mockMvc.perform(patch("/api/reviews/" + reviewId)
            .param("id", String.valueOf(reviewId))
            .header("Deokhugam-Request-User-ID", String.valueOf(MemberId))
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request))
        )
        .andExpect(status().isForbidden());
  }

  @Test
  @DisplayName("patch - 리뷰 수정 테스트 - 404 응답 확인")
  public void patchReviewWithNotFoundException() throws Exception {

    String newContent = "This is a test review";
    short newRating = (short) 5;
    PatchReviewRequest request = new PatchReviewRequest(newContent, newRating);

    Long reviewId = 1L;
    Long MemberId = 1L;

    given(reviewService.patchReview(any())).willThrow(ResourceNotFoundException.class);

    mockMvc.perform(patch("/api/reviews/" + reviewId)
            .param("id", String.valueOf(reviewId))
            .header("Deokhugam-Request-User-ID", String.valueOf(MemberId))
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request))
        )
        .andExpect(status().isNotFound());
  }


  @Test
  @DisplayName("patch - 리뷰 수정 테스트 - 500 응답 확인")
  public void patchReviewWithInternalServerException() throws Exception {

    String newContent = "This is a test review";
    short newRating = (short) 5;
    PatchReviewRequest request = new PatchReviewRequest(newContent, newRating);

    Long reviewId = 1L;
    Long MemberId = 1L;

    given(reviewService.patchReview(any())).willThrow(RuntimeException.class);

    mockMvc.perform(patch("/api/reviews/" + reviewId)
            .param("id", String.valueOf(reviewId))
            .header("Deokhugam-Request-User-ID", String.valueOf(MemberId))
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request))
        )
        .andExpect(status().isInternalServerError());

  }

}
