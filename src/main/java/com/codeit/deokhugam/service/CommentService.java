package com.codeit.deokhugam.service;

import com.codeit.deokhugam.common.exception.handler.CustomException;
import com.codeit.deokhugam.common.exception.handler.ErrorCode;
import com.codeit.deokhugam.domain.entity.Comment;
import com.codeit.deokhugam.domain.entity.Member;
import com.codeit.deokhugam.domain.entity.Review;
import com.codeit.deokhugam.dto.command.comment.CommentCreateCommand;
import com.codeit.deokhugam.dto.command.comment.CommentHardDeleteCommand;
import com.codeit.deokhugam.dto.command.comment.CommentSoftDeleteCommand;
import com.codeit.deokhugam.dto.command.comment.CommentUpdateCommand;
import com.codeit.deokhugam.dto.command.comment.CursorPageCommentCommand;
import com.codeit.deokhugam.dto.response.comment.CommentResponse;
import com.codeit.deokhugam.dto.result.comment.CommentCreateResult;
import com.codeit.deokhugam.dto.result.comment.CommentUpdateResult;
import com.codeit.deokhugam.dto.result.comment.CursorPageCommentResult;
import com.codeit.deokhugam.mapper.CommentMapper;
import com.codeit.deokhugam.repository.CommentRepository;
import com.codeit.deokhugam.repository.MemberRepository;
import com.codeit.deokhugam.repository.ReviewRepository;
import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CommentService {

  private final CommentRepository commentRepository;
  private final ReviewRepository reviewRepository;
  private final MemberRepository memberRepository;
  private final NotificationService notificationService;
  private final CommentMapper commentMapper;


  // 댓글 생성 기능 (기존 코드)
  @Transactional
  public CommentCreateResult createComment(CommentCreateCommand command, Long requestMemberId) {

    // 인가(Authorization) 검증: 요청자(헤더)와 작성자(Command)가 일치하는지 확인
    // 403 커스텀 예외
    if (!requestMemberId.equals(command.memberId())) {
      throw new CustomException(ErrorCode.COMMENT_NOT_AUTHORIZED);
    }

    // 리뷰 조회 없으면 404 커스텀 예외
    Review review = reviewRepository.findById(command.reviewId())
        .orElseThrow(() -> new CustomException(ErrorCode.COMMENT_REVIEW_NOT_FOUND));

    // 멤버 조회 없으면 404 커스텀 예외
    Member member = memberRepository.findById(command.memberId())
        .orElseThrow(() -> new CustomException(ErrorCode.COMMENT_USER_NOT_FOUND));

    //mapper에 있는 entity로 매핑해주는 메서드를 사용해서 entity로 변환
    Comment comment = commentMapper.toComment(command, review, member);

    // 댓글 DB에 저장
    Comment savedComment = commentRepository.save(comment);
    review.setCommentCount(review.getCommentCount() + 1);

    // 알림
    // 리뷰 작성자와 댓글 작성자가 다를 경우에만 알림 생성
    if (!review.getMember().getId().equals(member.getId())) {
      // NotificationService의 createCommentNotification 메소드 호출
      notificationService.createCommentNotification(review.getMember(), review, savedComment);
    }

    // mapper에 있는 Result로 매핑해주는 메서드를 사용해서 Result로 변환
    return commentMapper.toCommentCreateResult(savedComment);
  }

  //댓글 목록 조회
  @Transactional(readOnly = true)
  public CursorPageCommentResult findCommentsByReviewId(CursorPageCommentCommand command) {

    int limit = command.commentLimit();
    Long reviewId = command.reviewId();

    //404 예외
    if (!reviewRepository.existsById(reviewId)) {
      throw new CustomException(ErrorCode.COMMENT_REVIEW_NOT_FOUND);
    }

    //Repository에서 limit+1개 조회
    List<Comment> commentList = commentRepository.findByReviewId(
        command.reviewId(),
        command.direction(),
        command.after(),    // 보조 커서 (createdAt)
        command.cursorId(), // 메인 커서 (id)
        limit
    );

    // 다음 페이지 유무 판단
    boolean hasNext = commentList.size() > limit;

    // 실제 반환할 리스트 (limit 개수만큼만 자르기)
    List<Comment> commentListAfter = hasNext ? commentList.subList(0, limit) : commentList;

    // 다음 커서(nextAfter, nextCursorId) 계산
    Instant nextAfter = null;
    Long nextCursorId = null;
    if (hasNext) {
      Comment lastComment = commentListAfter.get(limit - 1);
      nextAfter = lastComment.getCreatedAt();
      nextCursorId = lastComment.getId();
    }

    // List<Comment> -> List<CommentResponse> 변환
    List<CommentResponse> commentListResponse = commentListAfter.stream()
        .map(commentMapper::toCommentListResponse)
        .collect(Collectors.toList());

    // Result 객체 생성 후 반환
    return new CursorPageCommentResult(
        commentListResponse,
        nextCursorId != null ? String.valueOf(nextCursorId) : null, // API 명세서에 맞춰 String 변환
        nextAfter,
        commentListResponse.size(),
        0L,
        hasNext
    );
  }

  // 댓글 상세 조회
  @Transactional(readOnly = true)
  public CommentResponse findCommentById(Long commentId) {

    // commendID로 댓글 조회 (없으면 404 예외)
    Comment comment = commentRepository.findById(commentId)
        .orElseThrow(() -> new CustomException(ErrorCode.COMMENT_NOT_FOUND));

    // 논리 삭제된 댓글인지 확인 (deleted == true)
    if (comment.isDeleted()) {
      throw new CustomException(ErrorCode.COMMENT_NOT_FOUND);
    }

    //Entity -> Response DTO 변환
    return commentMapper.toCommentListResponse(comment);
  }

  // 댓글 수정
  @Transactional
  public CommentUpdateResult updateComment(CommentUpdateCommand command) {

    // 요청자 소유자 체크 헬퍼 메소드로 수정함
    Comment comment = findCommentAndCheckAuthority(command.commentId(), command.requestMemberId());

    // 논리 삭제된 댓글인지 확인 (deleted == true)
    if (comment.isDeleted()) {
      throw new CustomException(ErrorCode.COMMENT_NOT_FOUND);
    }

    // mapper를 사용해 엔티티 내용 업데이트
    comment.updateComment(command.content());

    return commentMapper.toCommentUpdateResult(comment);
  }

  // 댓글 논리 삭제
  @Transactional
  public void softDeleteComment(CommentSoftDeleteCommand command) {

    // 요청자 소유자 체크 헬퍼 메소드로 수정함
    Comment comment = findCommentAndCheckAuthority(command.commentId(), command.requestMemberId());
    Review review = comment.getReview();
    review.setCommentCount(review.getCommentCount() - 1);
    // 2. 엔티티의 softDelete() 메소드 호출 (deleted = true로 변경)
    comment.softDelete();
  }

  // 댓글 물리 삭제
  @Transactional
  public void hardDeleteComment(CommentHardDeleteCommand command) {

    // 요청자 소유자 체크 헬퍼 메소드로 수정함
    Comment comment = findCommentAndCheckAuthority(command.commentId(), command.requestMemberId());

    // 물리 삭제 실행
    commentRepository.delete(comment);
  }


  // 요청자 소유자 체크 헬퍼 메소드로 수정함
  private Comment findCommentAndCheckAuthority(Long commentId, Long requestMemberId) {
    // 댓글 조회 (404)
    Comment comment = commentRepository.findById(commentId)
        .orElseThrow(() -> new CustomException(ErrorCode.COMMENT_NOT_FOUND));

    // 권한 검증 (403)
    if (!comment.getMember().getId().equals(requestMemberId)) {
      throw new CustomException(ErrorCode.COMMENT_NOT_AUTHORIZED);
    }

    return comment;
  }
}
