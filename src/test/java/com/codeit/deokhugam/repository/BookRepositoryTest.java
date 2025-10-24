package com.codeit.deokhugam.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.codeit.deokhugam.domain.entity.Book;
import com.codeit.deokhugam.fixture.BookFixture;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
class BookRepositoryTest extends DataBaseConnectionSupport {

  @Autowired
  private BookRepository bookRepository;

  @AfterEach
  void tearDown() {
    bookRepository.deleteAllInBatch();
  }

  @Test
  @DisplayName("도서 ID로 상세 정보 조회 - 성공")
  void findById_Success() {
    // given
    Book book = BookFixture.createBookWithAllFields();
    Book savedBook = bookRepository.save(book);

    // when
    Optional<Book> result = bookRepository.findById(savedBook.getId());

    // then
    assertThat(result).isPresent();
    assertThat(result.get().getId()).isEqualTo(savedBook.getId());
    assertThat(result.get().getTitle()).isEqualTo("테스트 도서");
    assertThat(result.get().getAuthor()).isEqualTo("테스트 저자");
    assertThat(result.get().getDescription()).isEqualTo("테스트 설명");
    assertThat(result.get().getPublisher()).isEqualTo("테스트 출판사");
    assertThat(result.get().getPublishedDate()).isEqualTo(LocalDate.of(1990, 12, 30));
    assertThat(result.get().getIsbn()).isEqualTo("1234567890123");
    assertThat(result.get().getThumbnailUrl()).isEqualTo("testThumbnailUrl.png");
    assertThat(result.get().getReviewCount()).isEqualTo(5);
    assertThat(result.get().getRating()).isEqualByComparingTo(new BigDecimal("2.31"));
    assertThat(result.get().isDeleted()).isFalse();
    assertThat(result.get().getCreatedAt()).isNotNull();
  }

  @Test
  @DisplayName("도서 ID로 상세 정보 조회 - 실패")
  void findById_Failure() {
    // given
    Long bookId = 99999L;

    // when
    Optional<Book> result = bookRepository.findById(bookId);

    // then: 빈 Optional 반환
    assertThat(result).isEmpty();
  }
}