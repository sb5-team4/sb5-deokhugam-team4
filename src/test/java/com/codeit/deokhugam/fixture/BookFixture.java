package com.codeit.deokhugam.fixture;

import com.codeit.deokhugam.domain.entity.Book;
import java.math.BigDecimal;
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
}