package com.codeit.deokhugam.service;


import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.codeit.deokhugam.domain.entity.Comment;
import com.codeit.deokhugam.domain.entity.Member;
import com.codeit.deokhugam.domain.entity.Review;
import com.codeit.deokhugam.dto.command.CommentCreateCommand;
import com.codeit.deokhugam.dto.result.CommentCreateResult;
import com.codeit.deokhugam.mapper.CommentMapper;
import com.codeit.deokhugam.repository.CommentRepository;
import com.codeit.deokhugam.repository.MemberRepository;
import com.codeit.deokhugam.repository.ReviewRepository;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

// Mockito 사용
@ExtendWith(MockitoExtension.class)
public class CommentServiceTest {

  // 테스트 대상 CommentService Mock들을 주입받는다.
  @InjectMocks
  private CommentService commentService;

  // Mock
  @Mock
  private CommentRepository commentRepository;
  @Mock
  private ReviewRepository reviewRepository;
  @Mock
  private MemberRepository memberRepository;
  @Mock
  private NotificationService notificationService;
  @Mock
  private CommentMapper commentMapper;

  // Test용 데이터
  private Member reviewOwner;
  private Member commentWriter;
  private Review review;
  private Comment comment;
  private CommentCreateCommand commentCreateCommand;
  private CommentCreateResult commentCreateResult;

  // Test에 사용할 객체 생성
  @BeforeEach
  public void setup() {
    reviewOwner = Member.builder().id(1L).nickname("리뷰 쓴 사람").build();
    review = Review.builder().id(11L).member(reviewOwner).content("책 좋습니다").build();

    commentWriter = Member.builder().id(2L).nickname("댓글 쓴 사람").build();
    comment = Comment.builder().id(22L).review(review).member(commentWriter).content("리뷰 좋았습니다.").build();

    // command 객체 초기화
    commentCreateCommand = new CommentCreateCommand(review.getId(), commentWriter.getId(), "리뷰 좋았습니다.");

    // Mapper를 통해 Request -> Command 변환 하는건 Controller의 영역이니까 Command로 변환은 잘된다고 판단

    // mapper를 통해 Command -> Result로 매핑해주는 메서드를 사용해서 Result로 변환 하지 않고 직접 생성한다
    commentCreateResult = new CommentCreateResult(
        comment.getId(),
        review.getId(),
        commentWriter.getId(),
        commentWriter.getNickname(),
        comment.getContent(),
        null, // createdAt (테스트에 필요하면 채움)
        null           // updatedAt (테스트에 필요하면 채움)
    );

    //Mapper를 통해 Result -> Response 변환 하는것 또한 Controller의 영역이니까 Response로 변환은 잘된다고 판단

  }

  @Test
  @DisplayName("댓글 생성 성공 - 타인 리뷰에 댓글 시 알림이 생성된다.")
  public void createCommentSuccessWithNotification() {
    // given 준비 : Repository와 Mapper가 어떻게 행동할지 정의
    when(memberRepository.findById(commentWriter.getId())).thenReturn(Optional.of(commentWriter));
    when(reviewRepository.findById(review.getId())).thenReturn(Optional.of(review));
    when(commentMapper.toComment(commentCreateCommand, review, commentWriter)).thenReturn(comment);
    when(commentRepository.save(comment)).thenReturn(comment);
    when(commentMapper.toCommentCreateResult(comment)).thenReturn(commentCreateResult);

    // when 실행 : 서비스 로직 호출
    CommentCreateResult result = commentService.createComment(commentCreateCommand);

    // then 검증 : 결과 확인
    // result.id() (생성된 댓글 ID, 22L)는 기대했던 commentCreateResult.id() (댓글 ID, 22L)와 비교한다.
    assertThat(result.id()).isEqualTo(commentCreateResult.id());
    assertThat(result.content()).isEqualTo(commentCreateResult.content());

    // NotificationService 1번 호출 확인
    verify(notificationService, times(1)).create(reviewOwner, review, comment);
  }

}