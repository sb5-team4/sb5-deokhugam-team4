package com.codeit.deokhugam.controller;

import com.codeit.deokhugam.common.exception.AuthorizationException;
import com.codeit.deokhugam.dto.command.CommentCreateCommand;
import com.codeit.deokhugam.dto.request.CommentCreateRequest;
import com.codeit.deokhugam.dto.response.CommentResponse;
import com.codeit.deokhugam.dto.result.CommentCreateResult;
import com.codeit.deokhugam.mapper.CommentMapper;
import com.codeit.deokhugam.service.CommentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/comments")
@RequiredArgsConstructor
public class CommentController {

  private final CommentService commentService;
  private final CommentMapper commentMapper;


  // 댓글 생성
  @PostMapping
  public ResponseEntity<CommentResponse> createComment(
      // 요청자 멤버ID
      @RequestHeader("Deokhugam-Request-User-ID") Long requestMemberId,
      // Validation 검증
      @Valid @RequestBody CommentCreateRequest request
  ) {

    // 요청자와 작성자 일치 검증
    if (!requestMemberId.equals(request.getMemberId())) {
      throw new AuthorizationException("댓글 작성 권한이 없습니다.");
    }

    // Mapper를 통해 Request -> Command 변환
    CommentCreateCommand command = commentMapper.toCommentCreateCommand(request, requestMemberId);

    // Service 호출 댓글 생성 메서드 로직 실행후 Result로 변환
    CommentCreateResult result = commentService.createComment(command);

    // Mapper를 통해 Result -> Response 변환
    CommentResponse response = commentMapper.toCommentResponse(result);

    // 201 Created 상태 코드와 Response 요청자에게 반환
    return ResponseEntity.status(HttpStatus.CREATED).body(response);
  }
}