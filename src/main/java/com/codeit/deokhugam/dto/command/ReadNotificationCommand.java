package com.codeit.deokhugam.dto.command;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@AllArgsConstructor
@Getter
@Builder
public class ReadNotificationCommand {

  Long notificationId;
  Long loginMemberId;
  boolean confirmed;

}
