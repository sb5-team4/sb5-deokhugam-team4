package com.codeit.deokhugam.service;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.codeit.deokhugam.domain.entity.Book;
import com.codeit.deokhugam.dto.request.BookUpdateRequest;
import com.codeit.deokhugam.dto.response.BookResponse;
import com.codeit.deokhugam.fixture.BookFixture;
import com.codeit.deokhugam.mapper.BookMapper;
import com.codeit.deokhugam.repository.BookRepository;
import java.time.LocalDate;
import java.util.NoSuchElementException;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

@ExtendWith(MockitoExtension.class)
@DisplayName("BookService 단위 테스트")
public class BookServiceTest {

  @Mock
  private BookRepository bookRepository;

  @Mock
  private BookMapper bookMapper;

  @Mock
  private S3Service s3Service;

  @Mock
  private MultipartFile thumbnailImage;

  @InjectMocks
  private BookService bookService;

  private Book testBook;
  private BookUpdateRequest testBookUpdateRequest;

  @BeforeEach
  void setUp() {
    testBook = BookFixture.createBookWithAllFields();

    testBookUpdateRequest = new BookUpdateRequest();
    testBookUpdateRequest.setTitle("수정된 제목");
    testBookUpdateRequest.setAuthor("수정된 저자");
    testBookUpdateRequest.setDescription("수정된 소개");
    testBookUpdateRequest.setPublisher("수정된 출판사");
    testBookUpdateRequest.setPublishedDate(LocalDate.of(2000, 01, 01));
  }

  @Test
  @DisplayName("도서 정보 수정(썸네일 제외) - 성공")
  void updateBookWithoutImage_Success() {
    // Given
    when(bookRepository.findById(1L)).thenReturn(Optional.of(testBook));
    when(bookRepository.save(any(Book.class))).thenReturn(testBook);

    // When
    BookResponse response = bookService.updateBook(1L, testBookUpdateRequest, null);

    // Then
    assertThat(response).isNotNull();
    verify(bookRepository).findById(1L);
    verify(bookMapper).toBookUpdateCommand(testBookUpdateRequest);
    verify(bookMapper).updateBookFromCommand(any(), any(Book.class));
    verify(s3Service, never()).uploadFile(any());
    verify(bookRepository).save(testBook);
  }

  @Test
  @DisplayName("도서 정보 수정(썸네일 포함) - 성공")
  void updateBookWithImage_Success() {
    // Given
    when(bookRepository.findById(1L)).thenReturn(Optional.of(testBook));
    when(bookRepository.save(any(Book.class))).thenReturn(testBook);

    // When
    BookResponse response = bookService.updateBook(1L, testBookUpdateRequest, thumbnailImage);

    // Then
    assertThat(response).isNotNull();
    verify(bookRepository).findById(1L);
    verify(bookMapper).toBookUpdateCommand(testBookUpdateRequest);
    verify(bookMapper).updateBookFromCommand(any(), any(Book.class));
    verify(s3Service).uploadFile(thumbnailImage);
    verify(bookRepository).save(testBook);
  }

  @Test
  @DisplayName("도서 정보 수정 - 404 실패 (존재하지 않는 아이디)")
  void updateBook_NotFound() {
    // Given
    when(bookRepository.findById(99999L)).thenReturn(Optional.empty());

    // When & Then
    assertThatThrownBy(() -> bookService.updateBook(99999L, testBookUpdateRequest, thumbnailImage))
        .isInstanceOf(NoSuchElementException.class)
        .hasMessageContaining("해당하는 도서 ID가 존재하지 않습니다");

    verify(bookRepository).findById(99999L);
    verify(bookRepository, never()).save(any());
  }
}
