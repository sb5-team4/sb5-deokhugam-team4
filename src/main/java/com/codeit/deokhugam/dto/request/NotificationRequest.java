package com.codeit.deokhugam.dto.request;


import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@AllArgsConstructor
@Getter
@Builder
public class NotificationRequest {

  @NotNull
  private final Boolean confirmed;

}
