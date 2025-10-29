package com.codeit.deokhugam.service;

import com.codeit.deokhugam.common.exception.AuthorizationException;
import com.codeit.deokhugam.common.exception.ResourceNotFoundException;
import com.codeit.deokhugam.domain.entity.Comment;
import com.codeit.deokhugam.domain.entity.Member;
import com.codeit.deokhugam.domain.entity.Review;
import com.codeit.deokhugam.dto.command.comment.CommentCreateCommand;
import com.codeit.deokhugam.dto.command.comment.CursorPageCommentCommand;
import com.codeit.deokhugam.dto.response.comment.CommentResponse;
import com.codeit.deokhugam.dto.result.comment.CommentCreateResult;
import com.codeit.deokhugam.dto.result.comment.CursorPageCommentResult;
import com.codeit.deokhugam.mapper.CommentMapper;
import com.codeit.deokhugam.repository.CommentRepository;
import com.codeit.deokhugam.repository.MemberRepository;
import com.codeit.deokhugam.repository.ReviewRepository;
import java.time.Instant;
import java.util.List;
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


  // 댓글 생성 기능
  @Transactional
  public CommentCreateResult createComment(CommentCreateCommand command, Long requestMemberId) {

    // 인가(Authorization) 검증: 요청자(헤더)와 작성자(Command)가 일치하는지 확인
    if (!requestMemberId.equals(command.memberId())) {
      throw new AuthorizationException("댓글 작성 권한이 없습니다.");
    }

    // 리뷰 조회 없으면 404 커스텀 예외
    Review review = reviewRepository.findById(command.reviewId())
        .orElseThrow(() -> new ResourceNotFoundException("Review", command.reviewId()));

    // 멤버 조회 없으면 404 커스텀 예외
    Member member = memberRepository.findById(command.memberId())
        .orElseThrow(() -> new ResourceNotFoundException("Member", command.memberId()));

    //mapper에 있는 entity로 매핑해주는 메서드를 사용해서 entity로 변환
    Comment comment = commentMapper.toComment(command, review, member);

    // 댓글 DB에 저장
    Comment savedComment = commentRepository.save(comment);


    // 알림
    // 리뷰 작성자와 댓글 작성자가 다를 경우에만 알림 생성
    if (!review.getMember().getId().equals(member.getId())) {

    // NotificationService의 create 메소드 호출
    notificationService.create(review.getMember(), review, savedComment);
    }

  // mapper에 있는 Result로 매핑해주는 메서드를 사용해서 Result로 변환
    return commentMapper.toCommentCreateResult(savedComment);
  }

  //댓글 목록 조회 (커서 기반 페이지네이션 / 무한 스크롤)
  @Transactional(readOnly = true)
  public CursorPageCommentResult findCommentsByReviewId(CursorPageCommentCommand command) {

    int limit = command.commentLimit();

    //Repository에서 limit+1개 조회
    List<Comment> commentList = commentRepository.findByCommentReviewIdWithCursor(
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
        .toList();

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

}
