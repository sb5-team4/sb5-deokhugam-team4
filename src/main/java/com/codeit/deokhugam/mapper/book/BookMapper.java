package com.codeit.deokhugam.mapper.book;

import com.codeit.deokhugam.domain.entity.Book;
import com.codeit.deokhugam.dto.command.book.BookCreateCommand;
import com.codeit.deokhugam.dto.command.book.BookListCommand;
import com.codeit.deokhugam.dto.command.book.BookUpdateCommand;
import com.codeit.deokhugam.dto.request.book.BookCreateRequest;
import com.codeit.deokhugam.dto.request.book.BookListRequest;
import com.codeit.deokhugam.dto.request.book.BookUpdateRequest;
import com.codeit.deokhugam.dto.response.book.BookListResponse;
import com.codeit.deokhugam.dto.response.book.BookListResponse.BookItem;
import com.codeit.deokhugam.dto.response.book.BookResponse;
import com.codeit.deokhugam.dto.response.book.NaverBookResponse;
import com.codeit.deokhugam.dto.response.book.NaverBookSearchResponse;
import com.codeit.deokhugam.dto.result.book.BookCreateResult;
import com.codeit.deokhugam.dto.result.book.BookInfoByIsbnResult;
import com.codeit.deokhugam.dto.result.book.BookListResult;
import com.codeit.deokhugam.dto.result.book.BookUpdateResult;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;

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

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "thumbnailUrl", ignore = true)
  @Mapping(target = "reviewCount", expression = "java(0L)")
  @Mapping(target = "rating", expression = "java(java.math.BigDecimal.ZERO)")
  @Mapping(target = "deleted", expression = "java(false)")
  @Mapping(target = "createdAt", ignore = true)
  @Mapping(target = "updatedAt", ignore = true)
  Book toEntity(BookCreateCommand command);

  BookUpdateCommand toBookUpdateCommand(BookUpdateRequest request);

  BookCreateCommand toBookCreateCommand(BookCreateRequest request);

  BookListCommand toBookListCommand(BookListRequest request);

  BookResponse toBookResponse(BookUpdateResult result);

  BookResponse toBookResponse(Book book);

  BookResponse toBookResponse(BookCreateResult result);

  NaverBookResponse toNaverBookResponse(BookInfoByIsbnResult result);

  @Mapping(target = "content", source = "content")
  BookListResponse toBookListResponse(BookListResult result);

  BookListResponse.BookItem toBookItem(BookListResult.BookResult bookResult);

  BookCreateResult toBookCreateResult(Book book);

  BookUpdateResult toBookUpdateResult(Book book);

  @Mapping(target = "title", source = "title", qualifiedByName = "removeHtmlTags")
  @Mapping(target = "author", source = "author", qualifiedByName = "removeHtmlTags")
  @Mapping(target = "publishedDate", source = "pubdate", qualifiedByName = "parsePublishedDate")
  @Mapping(target = "isbn", source = "isbn", qualifiedByName = "extractIsbn13")
  @Mapping(target = "thumbnailImage", source = "image")
  BookInfoByIsbnResult toBookInfoByIsbnResult(NaverBookSearchResponse.Item item);

  BookListResult.BookResult toBookResult(Book book);

  List<BookItem> toBookItemList(List<BookListResult.BookResult> bookResults);

  // Naver API가 반환하는 검색어의 HTML 태그 제거
  @Named("removeHtmlTags")
  default String removeHtmlTags(String text) {
    if (text == null) {
      return null;
    }
    return text.replaceAll("<[^>]*>", "");
  }

  // yyyyMMdd 날짜 문자열 LocalDate로 변환
  @Named("parsePublishedDate")
  default LocalDate parsePublishedDate(String pubDate) {
    if (pubDate == null || pubDate.length() != 8) {
      return null;
    }
    try {
      DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
      return LocalDate.parse(pubDate, formatter);
    } catch (Exception e) {
      return null;
    }
  }

  // 13자리 ISBN만 추출
  @Named("extractIsbn13")
  default String extractIsbn13(String isbn) {
    if (isbn == null) {
      return null;
    }
    String[] parts = isbn.split("\\s+");
    for (String part : parts) {
      if (part.length() == 13) {
        return part;
      }
    }
    return isbn;
  }
}