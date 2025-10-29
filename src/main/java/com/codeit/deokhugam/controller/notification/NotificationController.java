package com.codeit.deokhugam.controller.notification;

import com.codeit.deokhugam.dto.command.GetNotificationCommand;
import com.codeit.deokhugam.dto.command.ReadNotificationCommand;
import com.codeit.deokhugam.dto.request.NotificationRequest;
import com.codeit.deokhugam.dto.response.CursorPageResponse;
import com.codeit.deokhugam.dto.response.NotificationResponse;
import com.codeit.deokhugam.dto.result.GetNotificationResult;
import com.codeit.deokhugam.dto.result.ReadNotificationResult;
import com.codeit.deokhugam.mapper.NotificationMapper;
import com.codeit.deokhugam.service.NotificationService;
import java.time.Instant;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/notifications")
public class NotificationController {

  private final NotificationService notificationService;
  private final NotificationMapper notificationMapper;

  @PatchMapping("/{id}")
  public ResponseEntity<NotificationResponse> update(
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

  @PatchMapping("/read-all")
  public ResponseEntity<Void> updateAll(@RequestHeader("Deokhugam-Request-User-ID") Long memberId) {

    notificationService.readAll(memberId);

    return ResponseEntity.noContent().build();
  }

  @GetMapping
  public ResponseEntity<CursorPageResponse<NotificationResponse, Instant>> readAll(
      @RequestParam(name = "userId", required = false) Long authorId,
      @RequestParam(defaultValue = "DESC") Direction direction,
      @RequestParam(required = false) Instant cursor,
      @RequestParam(required = false) Instant after,
      @RequestParam(defaultValue = "50") Integer limit
  ) {
    // todo 좋아요를 계속 눌렀다가 취소하면 알람이 계속 생성됨

    GetNotificationResult result = notificationService.getAll(GetNotificationCommand.from(
        authorId,
        direction,
        cursor,
        after,
        limit
    ));

    CursorPageResponse<NotificationResponse, Instant> cursorPageResponse = CursorPageResponse.<NotificationResponse, Instant>from(
        result.getReviews().stream().map(notificationMapper::toResponse).toList(),
        result.getNextCursor(),
        result.getNextAfter(),
        result.getSize(),
        result.getTotalElements(),
        result.getHasNext()
    );

    return ResponseEntity.ok(cursorPageResponse);
  }


}
