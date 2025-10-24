package com.codeit.deokhugam.mapper;

import com.codeit.deokhugam.domain.entity.Book;
import com.codeit.deokhugam.dto.command.BookUpdateCommand;
import com.codeit.deokhugam.dto.request.BookUpdateRequest;
import com.codeit.deokhugam.dto.response.BookResponse;
import com.codeit.deokhugam.dto.result.BookUpdateResult;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface BookMapper {

  BookUpdateCommand toBookUpdateCommand(BookUpdateRequest request);

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "isbn", ignore = true)
  @Mapping(target = "thumbnailUrl", ignore = true)
  @Mapping(target = "reviewCount", ignore = true)
  @Mapping(target = "rating", ignore = true)
  @Mapping(target = "deleted", ignore = true)
  @Mapping(target = "createdAt", ignore = true)
  @Mapping(target = "updatedAt", ignore = true)
  void updateBookFromCommand(BookUpdateCommand command, @MappingTarget Book book);

  BookUpdateResult toBookUpdateResult(Book book);

  BookResponse toBookResponse(BookUpdateResult result);

  BookResponse toBookResponse(Book book);
}