package com.codeit.deokhugam.service;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.codeit.deokhugam.domain.entity.Book;
import com.codeit.deokhugam.dto.command.BookUpdateCommand;
import com.codeit.deokhugam.dto.result.BookUpdateResult;
import com.codeit.deokhugam.fixture.BookFixture;
import com.codeit.deokhugam.mapper.BookMapper;
import com.codeit.deokhugam.repository.BookRepository;
import java.math.BigDecimal;
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
  private BookUpdateCommand testBookUpdateCommand;
  private BookUpdateResult testBookUpdateResult;

  @BeforeEach
  void setUp() {
    testBook = BookFixture.createBookWithAllFields();

    testBookUpdateCommand = BookUpdateCommand.builder()
        .title("수정된 제목")
        .author("수정된 저자")
        .description("수정된 소개")
        .publisher("수정된 출판사")
        .publishedDate(LocalDate.of(2000, 1, 1))
        .build();

    testBookUpdateResult = BookUpdateResult.builder()
        .id(1L)
        .title("수정된 제목")
        .author("수정된 저자")
        .description("수정된 소개")
        .publisher("수정된 출판사")
        .publishedDate(LocalDate.of(2000, 1, 1))
        .isbn("1234567890123")
        .thumbnailUrl("testThumbnailUrl.png")
        .reviewCount(5)
        .rating(new BigDecimal("2.31"))
        .build();
  }

  @Test
  @DisplayName("도서 정보 수정(썸네일 제외) - 성공")
  void updateBookWithoutImage_Success() {
    // Given
    when(bookRepository.findById(1L)).thenReturn(Optional.of(testBook));
    when(bookRepository.save(any(Book.class))).thenReturn(testBook);
    when(bookMapper.toBookUpdateResult(any(Book.class))).thenReturn(testBookUpdateResult);

    // When
    BookUpdateResult result = bookService.updateBook(1L, testBookUpdateCommand, null);

    // Then
    assertThat(result).isNotNull();
    assertThat(result.getTitle()).isEqualTo("수정된 제목");
    assertThat(result.getAuthor()).isEqualTo("수정된 저자");

    verify(bookRepository).findById(1L);
    verify(bookMapper).updateBookFromCommand(testBookUpdateCommand, testBook);
    verify(s3Service, never()).uploadFile(any()); // S3 업로드는 호출되지 않아야 함
    verify(bookRepository).save(testBook);
    verify(bookMapper).toBookUpdateResult(testBook);
  }

  @Test
  @DisplayName("도서 정보 수정(썸네일 포함) - 성공")
  void updateBookWithImage_Success() {
    // Given
    String uploadedUrl = "https://s3.amazonaws.com/bucket/new-thumbnail.png";

    when(bookRepository.findById(1L)).thenReturn(Optional.of(testBook));
    when(thumbnailImage.isEmpty()).thenReturn(false); // 이미지가 비어있지 않음
    when(s3Service.uploadFile(thumbnailImage)).thenReturn(uploadedUrl);
    when(bookRepository.save(any(Book.class))).thenReturn(testBook);
    when(bookMapper.toBookUpdateResult(any(Book.class))).thenReturn(testBookUpdateResult);

    // When
    BookUpdateResult result = bookService.updateBook(1L, testBookUpdateCommand, thumbnailImage);

    // Then
    assertThat(result).isNotNull();

    verify(bookRepository).findById(1L);
    verify(bookMapper).updateBookFromCommand(testBookUpdateCommand, testBook);
    verify(s3Service).uploadFile(thumbnailImage);
    verify(bookRepository).save(testBook);
    verify(bookMapper).toBookUpdateResult(testBook);
  }

  @Test
  @DisplayName("도서 정보 수정 - 404 실패 (존재하지 않는 아이디)")
  void updateBook_NotFound() {
    // Given
    when(bookRepository.findById(99999L)).thenReturn(Optional.empty());

    // When & Then
    assertThatThrownBy(() -> bookService.updateBook(99999L, testBookUpdateCommand, thumbnailImage))
        .isInstanceOf(NoSuchElementException.class)
        .hasMessageContaining("해당하는 도서 ID가 존재하지 않습니다");

    verify(bookRepository).findById(99999L);
    verify(bookRepository, never()).save(any());
    verify(bookMapper, never()).updateBookFromCommand(any(), any());
    verify(s3Service, never()).uploadFile(any());
  }

  @Test
  @DisplayName("도서 정보 수정 - 400 실패 (삭제된 도서)")
  void updateBook_DeletedBook() {
    // Given
    Book deletedBook = BookFixture.createDeletedBook();
    when(bookRepository.findById(1L)).thenReturn(Optional.of(deletedBook));

    // When & Then
    assertThatThrownBy(() -> bookService.updateBook(1L, testBookUpdateCommand, thumbnailImage))
        .isInstanceOf(IllegalStateException.class)
        .hasMessageContaining("삭제된 도서는 수정할 수 없습니다");

    verify(bookRepository).findById(1L);
    verify(bookRepository, never()).save(any());
    verify(bookMapper, never()).updateBookFromCommand(any(), any());
    verify(s3Service, never()).uploadFile(any());
  }
}