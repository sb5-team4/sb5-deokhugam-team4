package com.codeit.deokhugam.controller.notification;

import com.codeit.deokhugam.dto.command.ReadNotificationCommand;
import com.codeit.deokhugam.dto.request.NotificationRequest;
import com.codeit.deokhugam.dto.response.NotificationResponse;
import com.codeit.deokhugam.dto.result.ReadNotificationResult;
import com.codeit.deokhugam.mapper.NotificationMapper;
import com.codeit.deokhugam.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/notifications")
public class NotificationController {

  private final NotificationService notificationService;
  private final NotificationMapper notificationMapper;

  @PatchMapping("/{id}")
  public ResponseEntity<NotificationResponse> read(
      @PathVariable Long id,
      @RequestHeader("Deokhugam-Request-User-ID") Long memberId,
      @RequestBody NotificationRequest request
  ) {

    ReadNotificationResult result = notificationService.read(
        ReadNotificationCommand.builder()
            .notificationId(id).loginMemberId(memberId).confirmed(request.getConfirmed())
            .build());

    return ResponseEntity.ok(notificationMapper.toResponse(result));
  }


}
