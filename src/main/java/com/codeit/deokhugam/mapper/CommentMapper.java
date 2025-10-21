package com.codeit.deokhugam.mapper;

import com.codeit.deokhugam.domain.entity.Comment;
import com.codeit.deokhugam.domain.entity.Member;
import com.codeit.deokhugam.domain.entity.Review;
import com.codeit.deokhugam.dto.command.CommentCreateCommand;
import com.codeit.deokhugam.dto.request.CommentCreateRequest;
import com.codeit.deokhugam.dto.response.CommentResponse;
import com.codeit.deokhugam.dto.result.CommentCreateResult;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CommentMapper {

  // CommentCreateRequest -> CommentCreateCommand로 매핑
  @Mapping(source = "request.reviewId", target = "reviewId")
  @Mapping(source = "memberId", target = "memberId")
  @Mapping(source = "request.content", target = "content")
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

}
