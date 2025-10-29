package com.codeit.deokhugam.fixture;

import com.codeit.deokhugam.domain.entity.Book;
import com.codeit.deokhugam.dto.result.BookCreateResult;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

public class BookFixture {


  // 필수 필드만 가지는 테스트 도서 생성
  public static Book createBook() {
    return Book.builder()
        .title("테스트 도서")
        .author("테스트 저자")
        .publisher("테스트 출판사")
        .publishedDate(LocalDate.of(2020, 1, 1))
        .reviewCount(0)
        .rating(BigDecimal.ZERO)
        .deleted(false)
        .build();
  }

  // 모든 필드를 가지는 테스트 도서 생성
  public static Book createBook(
      String title,
      String author,
      String description,
      String publisher,
      LocalDate publishedDate,
      String isbn,
      String thumbnailUrl,
      int reviewCount,
      BigDecimal rating
  ) {
    return Book.builder()
        .title(title)
        .author(author)
        .description(description)
        .publisher(publisher)
        .publishedDate(publishedDate)
        .isbn(isbn)
        .thumbnailUrl(thumbnailUrl)
        .reviewCount(reviewCount)
        .rating(rating)
        .deleted(false)
        .build();
  }

  // 모든 필드가 채워진 테스트 도서
  public static Book createBookWithAllFields() {
    return Book.builder()
        .title("테스트 도서")
        .author("테스트 저자")
        .description("테스트 설명")
        .publisher("테스트 출판사")
        .publishedDate(LocalDate.of(1990, 12, 30))
        .isbn("1234567890123")
        .thumbnailUrl("testThumbnailUrl.png")
        .reviewCount(5)
        .rating(new BigDecimal("2.31"))
        .deleted(false)
        .build();
  }

  // 논리 삭제된 테스트 도서
  public static Book createDeletedBook() {
    return Book.builder()
        .title("삭제된 도서")
        .author("삭제된 저자")
        .publisher("삭제된 출판사")
        .publishedDate(LocalDate.of(2020, 1, 1))
        .reviewCount(5)
        .rating(new BigDecimal("4.5"))
        .deleted(true)
        .build();
  }

  // ISBN을 가지는 테스트 도서
  public static Book createBookWithIsbn(String isbn) {
    return Book.builder()
        .title("ISBN 테스트 도서")
        .author("ISBN 테스트 저자")
        .publisher("ISBN 테스트 출판사")
        .publishedDate(LocalDate.of(2020, 1, 1))
        .isbn("1234567890123")
        .reviewCount(0)
        .rating(BigDecimal.ZERO)
        .deleted(false)
        .build();
  }

  // ID를 지정할 수 있는 테스트 도서
  public static Book createBookWithId(Long id) {
    Book book = createBook();
    org.springframework.test.util.ReflectionTestUtils.setField(book, "id", id);
    return book;
  }

  // ID를 지정할 수 있으며 논리 삭제된 테스트 도서
  public static Book createDeletedBookWithId(Long id) {
    Book book = createDeletedBook();
    org.springframework.test.util.ReflectionTestUtils.setField(book, "id", id);
    return book;
  }

  // ID를 지정할 수 있고 모든 필드를 가지는 테스트 도서
  public static Book createBookWithAllFieldsAndId(Long id) {
    Book book = createBookWithAllFields();
    org.springframework.test.util.ReflectionTestUtils.setField(book, "id", id);
    return book;
  }


  // 모든 필드를 가지는 result
  public static BookCreateResult createBookCreateResult(Long id) {
    return BookCreateResult.builder()
        .id(id)
        .title("테스트 도서")
        .author("테스트 저자")
        .description("테스트 설명")
        .publisher("테스트 출판사")
        .publishedDate(LocalDate.of(1990, 12, 30))
        .isbn("1234567890123")
        .thumbnailUrl("testThumbnailUrl.png")
        .reviewCount(0)
        .rating(BigDecimal.ZERO)
        .createdAt(Instant.now())
        .updatedAt(Instant.now())
        .build();
  }


  // 필수 필드만 가지는 result
  public static BookCreateResult createBookCreateResultWithRequiredFields(Long id) {
    return BookCreateResult.builder()
        .id(id)
        .title("테스트 도서")
        .author("테스트 저자")
        .publisher("테스트 출판사")
        .publishedDate(LocalDate.of(2020, 1, 1))
        .reviewCount(0)
        .rating(BigDecimal.ZERO)
        .createdAt(Instant.now())
        .updatedAt(Instant.now())
        .build();
  }


  // thumbnailUrl을 가지는 result
  public static BookCreateResult createBookCreateResultWithThumbnail(Long id, String thumbnailUrl) {
    return BookCreateResult.builder()
        .id(id)
        .title("테스트 도서")
        .author("테스트 저자")
        .description("테스트 설명")
        .publisher("테스트 출판사")
        .publishedDate(LocalDate.of(1990, 12, 30))
        .isbn("1234567890123")
        .thumbnailUrl(thumbnailUrl)
        .reviewCount(0)
        .rating(BigDecimal.ZERO)
        .createdAt(Instant.now())
        .updatedAt(Instant.now())
        .build();
  }
}