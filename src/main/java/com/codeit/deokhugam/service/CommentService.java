package com.codeit.deokhugam.service;

import com.codeit.deokhugam.common.exception.ResourceNotFoundException;
import com.codeit.deokhugam.domain.entity.Comment;
import com.codeit.deokhugam.domain.entity.Member;
import com.codeit.deokhugam.domain.entity.Review;
import com.codeit.deokhugam.dto.command.CommentCreateCommand;
import com.codeit.deokhugam.dto.result.CommentCreateResult;
import com.codeit.deokhugam.mapper.CommentMapper;
import com.codeit.deokhugam.repository.CommentRepository;
import com.codeit.deokhugam.repository.MemberRepository;
import com.codeit.deokhugam.repository.ReviewRepository;
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
  public CommentCreateResult createComment(CommentCreateCommand command) {

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

}
