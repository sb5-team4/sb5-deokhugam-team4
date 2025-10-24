package com.codeit.deokhugam.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.codeit.deokhugam.common.exception.AuthorizationException;
import com.codeit.deokhugam.common.exception.ResourceNotFoundException;
import com.codeit.deokhugam.common.exception.handler.GlobalExceptionHandler;
import com.codeit.deokhugam.controller.review.ReviewController;
import com.codeit.deokhugam.dto.command.CreateReviewCommand;
import com.codeit.deokhugam.dto.request.review.CreateReviewRequest;
import com.codeit.deokhugam.dto.response.review.ReviewResponse;
import com.codeit.deokhugam.dto.result.CreateReviewResult;
import com.codeit.deokhugam.mapper.likeReview.LikeReviewMapper;
import com.codeit.deokhugam.mapper.review.ReviewMapper;
import com.codeit.deokhugam.service.LikeReviewService;
import com.codeit.deokhugam.service.ReviewService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import java.time.OffsetDateTime;
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
public class ReviewControllerTest {

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
  @DisplayName("post - 리뷰 생성 테스트")
  public void postReview() throws Exception {

    CreateReviewCommand command = CreateReviewCommand.builder()
        .bookId(bookId)
        .userId(userId)
        .content(content)
        .rating(rating)
        .build();

    CreateReviewResult result = CreateReviewResult.builder()
        .id(id)
        .bookId(bookId)
        .bookTitle(bookTitle)
        .bookThumbnailUrl(bookThumbnailUrl)
        .userId(userId)
        .userNickname(userNickname)
        .content(content)
        .rating(rating)
        .likeCount(likeCount)
        .commentCount(commentCount)
        .likedByMe(likedByMe)
        .createdAt(createdAt)
        .updatedAt(updatedAt)
        .build();
    ReviewResponse response = ReviewResponse.builder()
        .id(id)
        .bookId(bookId)
        .bookTitle(bookTitle)
        .bookThumbnailUrl(bookThumbnailUrl)
        .userId(userId)
        .userNickname(userNickname)
        .content(content)
        .rating(rating)
        .likeCount(likeCount)
        .commentCount(commentCount)
        .likedByMe(likedByMe)
        .createdAt(createdAt)
        .updatedAt(updatedAt)
        .build();

    given(reviewService.createReview(any())).willReturn(result);
    given(reviewMapper.toCommand(any())).willReturn(command);
    given(reviewMapper.toResponse(any())).willReturn(response);

    CreateReviewRequest request = CreateReviewRequest.builder()
        .bookId(bookId)
        .userId(userId)
        .content(content)
        .rating(rating)
        .build();

    MvcResult requestResult = mockMvc.perform(post("/api/reviews")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request))
        )
        .andExpect(jsonPath("$.id").value(id))
        .andExpect(jsonPath("$.bookId").value(bookId))
        .andExpect(jsonPath("$.bookTitle").value(bookTitle))
        .andExpect(jsonPath("$.bookThumbnailUrl").value(bookThumbnailUrl))
        .andExpect(jsonPath("$.userId").value(userId))
        .andExpect(jsonPath("$.userNickname").value(userNickname))
        .andExpect(jsonPath("$.content").value(content))
        .andExpect(jsonPath("$.rating").value("" + rating))
        .andExpect(jsonPath("$.likeCount").value(likeCount))
        .andExpect(jsonPath("$.commentCount").value(commentCount))
        .andExpect(jsonPath("$.likedByMe").value(likedByMe)).andReturn();

    String responseBody = requestResult.getResponse().getContentAsString();

// JSON에서 createdAt, updatedAt 추출
    OffsetDateTime actualCreatedAt = OffsetDateTime.parse(
        JsonPath.read(responseBody, "$.createdAt"));
    OffsetDateTime actualUpdatedAt = OffsetDateTime.parse(
        JsonPath.read(responseBody, "$.updatedAt"));

    assertEquals(createdAt.truncatedTo(ChronoUnit.MILLIS),
        actualCreatedAt.truncatedTo(ChronoUnit.MILLIS));
    assertEquals(updatedAt.truncatedTo(ChronoUnit.MILLIS),
        actualUpdatedAt.truncatedTo(ChronoUnit.MILLIS));
  }


  @Test
  @DisplayName("post - 리뷰 생성 테스트 - 404 응답 확인")
  public void postReviewWithNotFoundException() throws Exception {

    given(reviewService.createReview(any())).willThrow(ResourceNotFoundException.class);
    CreateReviewRequest request = CreateReviewRequest.builder()
        .bookId(bookId)
        .userId(userId)
        .content(content)
        .rating(rating)
        .build();
    mockMvc.perform(post("/api/reviews")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request))
        )
        .andExpect(status().isNotFound());

  }

  @Test
  @DisplayName("post - 리뷰 생성 테스트 - 500 응답 확인")
  public void postReviewWithInternalServerException() throws Exception {

    given(reviewService.createReview(any())).willThrow(RuntimeException.class);
    CreateReviewRequest request = CreateReviewRequest.builder()
        .bookId(bookId)
        .userId(userId)
        .content(content)
        .rating(rating)
        .build();
    mockMvc.perform(post("/api/reviews")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request))
        )
        .andExpect(status().isInternalServerError());

  }

  @Test
  @DisplayName("delete - 리뷰 논리 삭제 테스트")
  public void softDeleteReview() throws Exception {

    Long reviewId = 1L;
    Long MemberId = 1L;

    given(reviewService.softDelete(any())).willReturn(true);

    mockMvc.perform(delete("/api/reviews/" + reviewId)
            .param("id", String.valueOf(reviewId))
            .header("Deokhugam-Request-User-ID", String.valueOf(MemberId))
        )
        .andExpect(status().isNoContent());
  }


  @Test
  @DisplayName("delete - 리뷰 논리 삭제 테스트 - 잘못된 요청 (400 에러)")
  public void softDeleteReviewWithBadRequest() throws Exception {

    long reviewId = 1L;
    mockMvc.perform(delete("/api/reviews/" + reviewId)
        )
        .andExpect(status().isBadRequest());

  }

  @Test
  @DisplayName("delete - 리뷰 논리 삭제 테스트 - 권한없음 (403 에러)")
  public void softDeleteReviewWithNotAllowed() throws Exception {

    Long reviewId = 1L;
    Long MemberId = 1L;
    given(reviewService.softDelete(any())).willThrow(AuthorizationException.class);

    mockMvc.perform(delete("/api/reviews/" + reviewId)
            .param("id", String.valueOf(reviewId))
            .header("Deokhugam-Request-User-ID", String.valueOf(MemberId))
        )
        .andExpect(status().isForbidden());
  }

  @Test
  @DisplayName("delete - 리뷰 논리 삭제 테스트 - 리뷰 없음 (404 에러)")
  public void softDeleteReviewWithNotFound() throws Exception {
    Long reviewId = 1L;
    Long MemberId = 1L;
    given(reviewService.softDelete(any())).willThrow(ResourceNotFoundException.class);

    mockMvc.perform(delete("/api/reviews/" + reviewId)
            .param("id", String.valueOf(reviewId))
            .header("Deokhugam-Request-User-ID", String.valueOf(MemberId))
        )
        .andExpect(status().isNotFound());
  }

  @Test
  @DisplayName("delete - 리뷰 논리 삭제 테스트 - 예상치 못한 에러 (500 에러)")
  public void softDeleteReviewWithInternalServerError() throws Exception {

    Long reviewId = 1L;
    Long MemberId = 1L;
    given(reviewService.softDelete(any())).willThrow(RuntimeException.class);

    mockMvc.perform(delete("/api/reviews/" + reviewId)
            .param("id", String.valueOf(reviewId))
            .header("Deokhugam-Request-User-ID", String.valueOf(MemberId))
        )
        .andExpect(status().isInternalServerError());
  }
}
