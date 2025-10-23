package com.codeit.deokhugam.service;

import com.codeit.deokhugam.domain.entity.Comment;
import com.codeit.deokhugam.domain.entity.Member;
import com.codeit.deokhugam.domain.entity.Notification;
import com.codeit.deokhugam.domain.entity.Review;
import com.codeit.deokhugam.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class NotificationService {

  private final NotificationRepository notificationRepository;

  //알림 생성 메서드
  @Transactional(propagation = Propagation.REQUIRES_NEW)
  public void create(Member recipient, Review review, Comment comment) {

    String content = comment.getMember().getNickname() + "님이 회원님의 리뷰에 댓글을 달았습니다.";

    Notification notification = Notification.builder()
        .member(recipient) // 알림 받는 사람
        .review(review)
        .content(content)
        .confirmed(false)
        .deleted(false)
        .build();

    notificationRepository.save(notification);
  }
}