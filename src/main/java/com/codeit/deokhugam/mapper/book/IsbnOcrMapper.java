package com.codeit.deokhugam.mapper.book;

import com.codeit.deokhugam.dto.command.book.IsbnOcrCommand;
import com.codeit.deokhugam.dto.request.book.IsbnOcrRequest;
import org.springframework.stereotype.Component;

@Component
public class IsbnOcrMapper {

  public IsbnOcrCommand toCommand(IsbnOcrRequest request) {
    return IsbnOcrCommand.builder()
        .image(request.getImage()) // 이미지 파일 매핑
        .build();
  }
}
