package com.codeit.deokhugam.mapper;

import com.codeit.deokhugam.domain.entity.Comment;
import com.codeit.deokhugam.domain.entity.Member;
import com.codeit.deokhugam.domain.entity.Review;
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
import java.time.Instant;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface CommentMapper {

  // 댓글 생성 매퍼------------------------------------------------------------------------------------
  // CommentCreateRequest -> CommentCreateCommand로 매핑
  @Mapping(source = "request.reviewId", target = "reviewId")
  @Mapping(source = "request.content", target = "content")
  @Mapping(source = "memberId", target = "memberId")
  CommentCreateCommand toCommentCreateCommand(CommentCreateRequest request, Long memberId);

  // CommentCreateCommand -> Comment로 매핑
  @Mapping(source = "command.content", target = "content")
  @Mapping(target = "id", ignore = true)
  @Mapping(target = "deleted", ignore = true)
  @Mapping(target = "createdAt", ignore = true)
  @Mapping(target = "updatedAt", ignore = true)
  @Mapping(source = "review", target = "review")
  @Mapping(source = "member", target = "member")
  Comment toComment(CommentCreateCommand command, Review review, Member member);

  // Comment -> CommentCreateResult로 매핑
  @Mapping(source = "comment.review.id", target = "reviewId")
  @Mapping(source = "comment.member.id", target = "memberId")
  @Mapping(source = "comment.member.nickname", target = "nickname")
  CommentCreateResult toCommentCreateResult(Comment comment);

  // CommentCreateResult -> CommentResponse로 매핑
  CommentResponse toCommentResponse(CommentCreateResult result);

  // 댓글 목록 조회 매퍼---------------------------------------------------------------------------------

  //Controller: Request Params -> Command (댓글 목록 조회)
  @Mapping(source = "limit", target = "commentLimit")
  CursorPageCommentCommand toCursorPageCommentCommand(Long reviewId, String direction, Instant after, Long cursorId, int limit);

  //Service: Entity List -> DTO List (댓글 목록 조회)
  @Mapping(source = "comment.review.id", target = "reviewId")
  @Mapping(source = "comment.member.id", target = "memberId")
  @Mapping(source = "comment.member.nickname", target = "nickname")
  CommentResponse toCommentListResponse(Comment comment);

  //CursorPageCommentResult는 Service에서 한다

  //Controller: Result -> Response (목록 조회)
  CursorPageCommentResponse toCursorPageCommentResponse(CursorPageCommentResult result);

  // 댓글 수정----------------------------------------------------------------------------------------

  // controller: CommentUpdateRequest -> CommentUpdateCommand로 매핑
  @Mapping(source = "request.content", target = "content")
  CommentUpdateCommand toCommentUpdateCommand(CommentUpdateRequest request, Long commentId, Long requestMemberId);

  // service: entity -> CommentUpdateResult로 매핑
  @Mapping(source = "comment.review.id", target = "reviewId")
  @Mapping(source = "comment.member.id", target = "memberId")
  CommentUpdateResult toCommentUpdateResult(Comment comment);

  // controller: CommentUpdateResult -> CommentUpdateResponse로 매핑
  CommentResponse toCommentUpdateResponse(CommentUpdateResult result);

  // service: CommentUpdateCommand -> 기존 entity로 update
  void updateCommentFromCommand(CommentUpdateCommand command, @MappingTarget Comment comment);

}
