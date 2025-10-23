package com.codeit.deokhugam.controller;


import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.codeit.deokhugam.common.exception.AuthorizationException;
import com.codeit.deokhugam.common.exception.ResourceNotFoundException;
import com.codeit.deokhugam.dto.request.CommentCreateRequest;
import com.codeit.deokhugam.dto.response.CommentResponse;
import com.codeit.deokhugam.dto.result.CommentCreateResult;
import com.codeit.deokhugam.mapper.CommentMapper;
import com.codeit.deokhugam.service.CommentService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

// CommentController 테스트 설정
@WebMvcTest(CommentController.class)

public class CommentControllerTest {

  // API 테스트를 위한 가짜 HTTP 호출 객체 설정
  @Autowired
  private MockMvc mockMvc;

  // JSON 변환 객체 설정
  @Autowired
  private ObjectMapper  objectMapper;

  // 컨트롤러 의존 가짜 빈
  @MockitoBean
  private CommentService commentService;
  @MockitoBean
  private CommentMapper commentMapper;

  // @EnableJpaAuditing 때문에 @WebMvcTest 실행 시 필요한 JpaMetamodelMappingContext 빈을 Mock으로 대신 주입
  // 이거없으면 테스트 안됨
  @MockitoBean
  JpaMetamodelMappingContext jpaMetamodelMappingContext;

  @Test
  @DisplayName("댓글 생성 성공 - (201 Created)")
  void createCommentSuccess() throws Exception {

    //Controller에서 사용하는 Request, Service로 받아온 Result, Response 적용
    //Controller는 Service/Mapper의 로직을 모르므로, 단순 반환만 설정
    CommentCreateRequest commentCreateRequest = new CommentCreateRequest(11L, 2L, "테스트 댓글");

    CommentCreateResult commentCreateResult = new CommentCreateResult(1L, 11L, 2L, "댓글 쓰는 사람", "테스트 댓글", null,null);

    CommentResponse commentResponse = new CommentResponse(1L, 11L, 2L, "댓글 쓰는 사람", "테스트 댓글", null,null);

    // Mapper를 통해 Request -> Command 변환 가정
    when(commentMapper.toCommentCreateCommand(any(), any())).thenReturn(null);

    // Service에서 Command를 받아서 코멘트 생성후 Result 반환 가정
    when(commentService.createComment(any())).thenReturn(commentCreateResult);

    // Mapper를 통해 Result -> Response 변환 가정
    when(commentMapper.toCommentResponse(any())).thenReturn(commentResponse);

    //검증
    mockMvc.perform(post("/api/comments")
            .header("Deokhugam-Request-User-ID", 2L) // 헤더 설정 memberId 일치 여부
            .contentType(MediaType.APPLICATION_JSON) // JSON 타입 설정
            .content(objectMapper.writeValueAsString(commentCreateRequest))) // commentCreateRequest를 JSON 문자열로 변환

        .andExpect(status().isCreated()) // Created 201 상태 코드 검증
        .andExpect(jsonPath("$.id").value(1L)) // 응답 JSON의 id 검증
        .andExpect(jsonPath("$.content").value("테스트 댓글")); // 응답 JSON의 댓글 검증

  }

  @Test
  @DisplayName("댓글 생성 실패 - 유효성 검사 실패 (400 Bad Request)")
  void createCommentBadRequest() throws Exception {

    // content가 비어있음 -> Request의 content @NotBlank 위반
    CommentCreateRequest commentCreateRequest = new CommentCreateRequest(11L, 2L, "");

    mockMvc.perform(post("/api/comments")
            .header("Deokhugam-Request-User-ID", 2L)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(commentCreateRequest)))

        .andExpect(status().isBadRequest()); // 400 Bad Request 검증

  }

  @Test
  @DisplayName("댓글 생성 실패 - 권한 없음 (403 Forbidden)")
  void createCommentForbidden() throws Exception {
    // 헤더 ID(1L)와 바디 ID(2L)가 다름
    CommentCreateRequest commentCreateRequest = new CommentCreateRequest(11L, 2L, "테스트 댓글");

    mockMvc.perform(post("/api/comments")
            .header("Deokhugam-Request-User-ID", 1L) // 헤더 ID(1L)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(commentCreateRequest)))

        .andExpect(status().isForbidden()) // 바디 ID(2L)
        .andExpect(result -> assertThat(result.getResolvedException())
            .isInstanceOf(AuthorizationException.class)); // 발생한 예외가 맞는지 검증
  }

  @Test
  @DisplayName("댓글 생성 실패 - 리뷰 없음 (404 Not Found)")
  void createCommentNotFoundReview() throws Exception {
    // Service가 404 예외를 던짐
    CommentCreateRequest request = new CommentCreateRequest(100L, 2L, "테스트 댓글");

    when(commentService.createComment(any()))
        .thenThrow(new ResourceNotFoundException("Review", 100L));

    mockMvc.perform(post("/api/comments")
            .header("Deokhugam-Request-User-ID", 2L)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))

        .andExpect(status().isNotFound()); // 404 Not Found 검증
  }

  @Test
  @DisplayName("댓글 생성 실패 - 멤버 없음 (404 Not Found)")
  void createCommentNotFoundMember() throws Exception {
    // Service가 404 예외를 던짐
    CommentCreateRequest request = new CommentCreateRequest(100L, 5L, "테스트 댓글");

    when(commentService.createComment(any()))
        .thenThrow(new ResourceNotFoundException("Member", 5L));

    mockMvc.perform(post("/api/comments")
            .header("Deokhugam-Request-User-ID", 5L)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))

        .andExpect(status().isNotFound()); // 404 Not Found 검증
  }


}
