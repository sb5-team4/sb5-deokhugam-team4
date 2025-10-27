package com.codeit.deokhugam.mapper;

import com.codeit.deokhugam.domain.entity.Book;
import com.codeit.deokhugam.dto.command.BookCreateCommand;
import com.codeit.deokhugam.dto.command.BookUpdateCommand;
import com.codeit.deokhugam.dto.request.BookCreateRequest;
import com.codeit.deokhugam.dto.request.BookUpdateRequest;
import com.codeit.deokhugam.dto.response.BookResponse;
import com.codeit.deokhugam.dto.result.BookCreateResult;
import com.codeit.deokhugam.dto.result.BookUpdateResult;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface BookMapper {

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

  BookUpdateCommand toBookUpdateCommand(BookUpdateRequest request);

  BookCreateCommand toBookCreateCommand(BookCreateRequest request);

  BookCreateResult toBookCreateResult(Book book);

  BookResponse toBookResponse(BookUpdateResult result);

  BookResponse toBookResponse(Book book);

  BookResponse toBookResponse(BookCreateResult result);

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "reviewCount", expression = "java(0)")
  @Mapping(target = "rating", expression = "java(java.math.BigDecimal.ZERO)")
  @Mapping(target = "deleted", expression = "java(false)")
  @Mapping(target = "createdAt", ignore = true)
  @Mapping(target = "updatedAt", ignore = true)
  Book toEntity(BookCreateCommand command);
}