package com.codeit.deokhugam.controller;

import com.codeit.deokhugam.dto.command.comment.CommentCreateCommand;
import com.codeit.deokhugam.dto.command.comment.CommentUpdateCommand;
import com.codeit.deokhugam.dto.command.comment.CursorPageCommentCommand;
import com.codeit.deokhugam.dto.request.comment.CommentCreateRequest;
import com.codeit.deokhugam.dto.request.comment.CommentUpdateRequest;
import com.codeit.deokhugam.dto.response.comment.CommentResponse;
import com.codeit.deokhugam.dto.response.comment.CursorPageCommentResponse;
import com.codeit.deokhugam.dto.result.comment.CommentCreateResult;
import com.codeit.deokhugam.dto.result.comment.CommentUpdateResult;
import com.codeit.deokhugam.dto.result.comment.CursorPageCommentResult;
import com.codeit.deokhugam.mapper.CommentMapper;
import com.codeit.deokhugam.service.CommentService;
import jakarta.validation.Valid;
import java.time.Instant;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/comments")
@RequiredArgsConstructor
public class CommentController {

  private final CommentService commentService;
  private final CommentMapper commentMapper;

  //댓글 생성 POST
  @PostMapping
  public ResponseEntity<CommentResponse> createComment(
      // 요청자 멤버ID
      @RequestHeader("Deokhugam-Request-User-ID") Long requestMemberId,
      // Validation 검증
      @Valid @RequestBody CommentCreateRequest request
  ) {

    // Mapper를 통해 Request -> Command 변환
    CommentCreateCommand command = commentMapper.toCommentCreateCommand(request, requestMemberId);

    // Service 호출 댓글 생성 메서드 로직 실행후 Result로 변환
    CommentCreateResult result = commentService.createComment(command, requestMemberId);

    // Mapper를 통해 Result -> Response 변환
    CommentResponse response = commentMapper.toCommentResponse(result);

    // 201 Created 상태 코드와 Response 요청자에게 반환
    return ResponseEntity.status(HttpStatus.CREATED).body(response);
  }

  // 댓글 목록 조회 GET
  @GetMapping
  public ResponseEntity<CursorPageCommentResponse> getAllComments(
      // --- API 명세서 Parameters ---
      @RequestParam("reviewId")
      Long reviewId,

      @RequestParam(value = "direction", defaultValue = "DESC")
      String direction,

      @RequestParam(value = "cursor", required = false)
      Long cursorId,

      @RequestParam(value = "after", required = false)
      Instant after,

      @RequestParam(value = "limit", defaultValue = "50")
      int limit
  ){

    // Mapper를 통해 파라미터 -> Command 객체로 변환
    CursorPageCommentCommand command = commentMapper.toCursorPageCommentCommand(
        reviewId, direction, after, cursorId, limit
    );
    // Service 호출
    CursorPageCommentResult result = commentService.findCommentsByReviewId(command);

    //Mapper를 통해 Result -> Response 변환
    CursorPageCommentResponse response = commentMapper.toCursorPageCommentResponse(result);

    // 200 OK 상태 코드와 Response 반환
    return ResponseEntity.ok(response);
  }

  // 댓글 상세 조회
  @GetMapping("/{commentId}")
  public ResponseEntity<CommentResponse> getCommentId(
      @PathVariable("commentId") Long commentId
  ) {
    CommentResponse response = commentService.findCommentById(commentId);
    return ResponseEntity.ok(response);
  }

  // 댓글 수정
  @PatchMapping("/{commentId}")
  public ResponseEntity<CommentResponse> updateComment(
      @PathVariable Long commentId,
      @RequestHeader("Deokhugam-Request-User-ID") Long requestMemberId,
      @Valid @RequestBody CommentUpdateRequest request
  ) {
    // mapper를 통해 request -> command로 변환
    CommentUpdateCommand command = commentMapper.toCommentUpdateCommand(request, commentId,  requestMemberId);

    // command를 대입하여 service update 로직 호출(service update 로직에서 수정,404,403 수행후 매퍼를 통해 result로 반환)
    CommentUpdateResult result = commentService.updateComment(command);

    // mapper를 통해 result -> response로 변환
    CommentResponse response = commentMapper.toCommentUpdateResponse(result);

    // 200 응답 반환
    return ResponseEntity.ok(response);
  }

}