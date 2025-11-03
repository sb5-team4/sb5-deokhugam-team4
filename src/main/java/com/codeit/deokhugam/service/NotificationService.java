package com.codeit.deokhugam.service;

import static com.codeit.deokhugam.common.exception.handler.ErrorCode.NOTIFICATION_NOT_AUTHORIZED;
import static com.codeit.deokhugam.common.exception.handler.ErrorCode.NOTIFICATION_NOT_FOUND;
import static com.codeit.deokhugam.common.exception.handler.ErrorCode.USER_NOT_FOUND;

import com.codeit.deokhugam.common.exception.handler.CustomException;
import com.codeit.deokhugam.domain.entity.Comment;
import com.codeit.deokhugam.domain.entity.Member;
import com.codeit.deokhugam.domain.entity.Notification;
import com.codeit.deokhugam.domain.entity.Review;
import com.codeit.deokhugam.dto.command.GetNotificationCommand;
import com.codeit.deokhugam.dto.command.ReadNotificationCommand;
import com.codeit.deokhugam.dto.result.GetNotificationOneResult;
import com.codeit.deokhugam.dto.result.GetNotificationResult;
import com.codeit.deokhugam.dto.result.PaginatedResult;
import com.codeit.deokhugam.dto.result.ReadNotificationResult;
import com.codeit.deokhugam.repository.MemberRepository;
import com.codeit.deokhugam.repository.NotificationRepository;
import java.time.Instant;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class NotificationService {

  private final NotificationRepository notificationRepository;
  private final MemberRepository memberRepository;

  //알림 생성 메서드
  @Transactional(propagation = Propagation.REQUIRES_NEW)
  public void create(Member recipient, Review review, Comment comment) {

    // todo 알람 설정에 맞게 content 설정
    String content = comment.getMember().getNickname() + "님이 회원님의 리뷰에 댓글을 달았습니다.";

    Notification notification = Notification.builder()
        .member(recipient) // 알림 받는 사람
        .review(review)
        .comment(comment)
        .content(content)
        .confirmed(false)
        .deleted(false)
        .build();

    notificationRepository.save(notification);
  }

  @Transactional
  public ReadNotificationResult read(ReadNotificationCommand command) {
    long id = command.getNotificationId();
    long memberId = command.getLoginMemberId();
    boolean confirmed = command.isConfirmed();

    Notification notification = notificationRepository.findByIdAndDeletedIsFalse(id)
        .orElseThrow(() -> new CustomException(NOTIFICATION_NOT_FOUND));

    notification.read(confirmed);

    Review review = notification.getReview();
    Member member = notification.getMember();

    if (!member.getId().equals(memberId)) {
      throw new CustomException(NOTIFICATION_NOT_AUTHORIZED);
    }

    return ReadNotificationResult.builder()
        .id(id)
        .userId(member.getId())
        .reviewId(notification.getReview().getId())
        .reviewTitle(review.getContent())
        .content(notification.getContent())
        .confirmed(notification.isConfirmed())
        .createdAt(notification.getCreatedAt())
        .updatedAt(notification.getUpdatedAt())
        .build();

  }


  @Transactional
  public void readAll(Long memberId) {
    // todo 알림이 매우 많을 때도 풀스캔을 할 것인가??
    // todo 업데이트 할때 bulkUpdate 로 최적화 가능

    Member member = memberRepository.findById(memberId)
        .orElseThrow(() -> new CustomException(USER_NOT_FOUND));

    List<Notification> notifications = notificationRepository.findByMemberId(memberId);
    boolean confirmed = true;
    notifications.forEach(notification -> {
      notification.read(confirmed);
    });

  }

  public GetNotificationResult getAll(GetNotificationCommand command) {
    Long userId = command.getUserId();
    Direction direction = command.getDirection();
    Instant cursor = command.getCursor();
    Instant after = command.getAfter();
    Integer limit = command.getLimit();

    PaginatedResult<Notification, Instant> entitiesResult = notificationRepository.searchWithCursor(
        userId, direction, cursor, after, limit
    );

    List<GetNotificationOneResult> notificationDetailResult = entitiesResult.getContent()
        .stream().map(notification
            -> GetNotificationOneResult.from(userId, notification.getReview(), notification))
        .toList();

    return GetNotificationResult.from(entitiesResult, notificationDetailResult);
  }
}