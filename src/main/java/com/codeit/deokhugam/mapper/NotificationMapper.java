package com.codeit.deokhugam.mapper;

import com.codeit.deokhugam.dto.response.NotificationResponse;
import com.codeit.deokhugam.dto.result.GetNotificationOneResult;
import com.codeit.deokhugam.dto.result.ReadNotificationResult;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface NotificationMapper {

  NotificationResponse toResponse(ReadNotificationResult result);

  NotificationResponse toResponse(GetNotificationOneResult result);

}
