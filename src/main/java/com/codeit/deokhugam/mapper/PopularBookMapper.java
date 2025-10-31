package com.codeit.deokhugam.mapper;

import com.codeit.deokhugam.domain.entity.PopularBook;
import com.codeit.deokhugam.dto.result.PopularBookListResult.PopularBookResult;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface PopularBookMapper {

  @Mapping(source = "book.id", target = "bookId")
  @Mapping(source = "book.title", target = "title")
  @Mapping(source = "book.author", target = "author")
  @Mapping(source = "book.thumbnailUrl", target = "thumbnailUrl")
  @Mapping(source = "book.reviewCount", target = "reviewCount")
  @Mapping(source = "book.rating", target = "rating")
  @Mapping(source = "rank", target = "rank")
  PopularBookResult toPopularBookResult(PopularBook popularBook);

}
