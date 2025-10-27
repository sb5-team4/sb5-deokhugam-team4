package com.codeit.deokhugam.service;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.codeit.deokhugam.common.exception.handler.CustomException;
import com.codeit.deokhugam.common.exception.handler.ErrorCode;
import com.codeit.deokhugam.domain.entity.Book;
import com.codeit.deokhugam.dto.command.BookCreateCommand;
import com.codeit.deokhugam.dto.command.BookUpdateCommand;
import com.codeit.deokhugam.dto.response.BookResponse;
import com.codeit.deokhugam.dto.result.BookUpdateResult;
import com.codeit.deokhugam.fixture.BookFixture;
import com.codeit.deokhugam.mapper.BookMapper;
import com.codeit.deokhugam.repository.BookRepository;
import java.math.BigDecimal;
import java.time.LocalDate;
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
        .isInstanceOf(CustomException.class)
        .hasFieldOrPropertyWithValue("errorCode", ErrorCode.BOOK_NOT_FOUND.getCode())
        .hasFieldOrPropertyWithValue("httpStatus", ErrorCode.BOOK_NOT_FOUND.getHttpStatus());

    verify(bookRepository).findById(99999L);
    verify(bookRepository, never()).save(any());
    verify(bookMapper, never()).updateBookFromCommand(any(), any());
    verify(s3Service, never()).uploadFile(any());
  }

  @Test
  @DisplayName("도서 정보 수정 - 400 실패 (삭제된 도서)")
  void updateBook_DeletedBook() {
    // Given
    Book deletedBook = BookFixture.createDeletedBookWithId(1L);
    when(bookRepository.findById(1L)).thenReturn(Optional.of(deletedBook));

    // When & Then
    assertThatThrownBy(() -> bookService.updateBook(1L, testBookUpdateCommand, thumbnailImage))
        .isInstanceOf(CustomException.class)
        .hasFieldOrPropertyWithValue("errorCode",
            ErrorCode.DELETED_BOOK_CANNOT_BE_MODIFIED.getCode())
        .hasFieldOrPropertyWithValue("httpStatus",
            ErrorCode.DELETED_BOOK_CANNOT_BE_MODIFIED.getHttpStatus());

    verify(bookRepository).findById(1L);
    verify(bookRepository, never()).save(any());
    verify(bookMapper, never()).updateBookFromCommand(any(), any());
    verify(s3Service, never()).uploadFile(any());
  }

  @Test
  @DisplayName("도서 논리 삭제 - 성공")
  void softDeleteBook_Success() {
    // Given
    Book book = BookFixture.createBookWithId(1L);
    when(bookRepository.findById(1L)).thenReturn(Optional.of(book));

    // When
    bookService.softDeleteBook(1L);

    // Then
    verify(bookRepository).findById(1L);
    verify(bookRepository).save(argThat(Book::isDeleted));
    assertThat(book.isDeleted()).isTrue();
  }

  @Test
  @DisplayName("도서 논리 삭제 - 404 실패 (존재하지 않는 ID)")
  void softDeleteBook_NotFound() {
    // Given
    Long bookId = 99999L;
    when(bookRepository.findById(bookId)).thenReturn(Optional.empty());

    // When & Then
    assertThatThrownBy(() -> bookService.softDeleteBook(bookId))
        .isInstanceOf(CustomException.class)
        .hasFieldOrPropertyWithValue("errorCode", ErrorCode.BOOK_NOT_FOUND.getCode())
        .hasFieldOrPropertyWithValue("httpStatus", ErrorCode.BOOK_NOT_FOUND.getHttpStatus());

    verify(bookRepository).findById(bookId);
    verify(bookRepository, never()).save(any());
  }

  @Test
  @DisplayName("도서 물리 삭제 - 성공")
  void hardDeleteBook_Success() {
    // Given
    Book book = BookFixture.createBookWithId(1L);
    when(bookRepository.findById(1L)).thenReturn(Optional.of(book));

    // When
    bookService.hardDeleteBook(1L);

    // Then
    verify(bookRepository).findById(1L);
    verify(bookRepository).delete(book);
  }

  @Test
  @DisplayName("도서 물리 삭제 - 논리 삭제된 도서의 물리 삭제 성공")
  void hardDeleteBook_AfterSoftDelete() {
    // Given
    Book book = BookFixture.createDeletedBookWithId(1L);
    when(bookRepository.findById(1L)).thenReturn(Optional.of(book));

    // When
    bookService.hardDeleteBook(1L);

    // Then
    verify(bookRepository).findById(1L);
    verify(bookRepository).delete(book);
  }

  @Test
  @DisplayName("도서 물리 삭제 - 404 실패 (존재하지 않는 ID)")
  void hardDeleteBook_NotFound() {
    // Given
    Long bookId = 99999L;
    when(bookRepository.findById(bookId)).thenReturn(Optional.empty());

    // When & Then
    assertThatThrownBy(() -> bookService.hardDeleteBook(bookId))
        .isInstanceOf(CustomException.class)
        .hasFieldOrPropertyWithValue("errorCode", ErrorCode.BOOK_NOT_FOUND.getCode())
        .hasFieldOrPropertyWithValue("httpStatus", ErrorCode.BOOK_NOT_FOUND.getHttpStatus());

    verify(bookRepository).findById(bookId);
    verify(bookRepository, never()).delete(any());
  }

  @Test
  @DisplayName("도서 등록 (모든 필드) - 성공")
  void createBookWithAllFields_Success() {
    // Given
    BookCreateCommand command = BookCreateCommand.builder()
        .title("테스트 도서")
        .author("테스트 저자")
        .description("테스트 설명")
        .publisher("테스트 출판사")
        .publishedDate(LocalDate.of(2020, 10, 1))
        .isbn("1234567890123")
        .thumbnailUrl("example.url.jpg")
        .build();

    when(bookRepository.findByIsbn(command.getIsbn())).thenReturn(Optional.empty());

    Book bookToSave = BookFixture.createBookWithAllFields();
    when(bookMapper.toEntity(command)).thenReturn(bookToSave);

    Book savedBook = BookFixture.createBookWithId(1L);
    when(bookRepository.save(any(Book.class))).thenReturn(savedBook);

    BookResponse expectedResponse = BookResponse.builder()
        .id(1L)
        .title(command.getTitle())
        .author(command.getAuthor())
        .description(command.getDescription())
        .publisher(command.getPublisher())
        .publishedDate(command.getPublishedDate())
        .isbn(command.getIsbn())
        .thumbnailUrl(command.getThumbnailUrl())
        .reviewCount(0)
        .rating(BigDecimal.ZERO)
        .build();
    when(bookMapper.toBookResponse(any(Book.class))).thenReturn(expectedResponse);

    // When
    BookResponse response = bookService.createBook(command);

    // Then
    assertThat(response).isNotNull();
    assertThat(response.getId()).isEqualTo(1L);
    assertThat(response.getTitle()).isEqualTo(command.getTitle());
    assertThat(response.getAuthor()).isEqualTo(command.getAuthor());
    assertThat(response.getReviewCount()).isEqualTo(0);
    assertThat(response.getRating()).isEqualByComparingTo(BigDecimal.ZERO);

    verify(bookRepository).findByIsbn(command.getIsbn());
    verify(bookMapper).toEntity(command);
    verify(bookRepository).save(any(Book.class));
    verify(bookMapper).toBookResponse(any(Book.class));
  }

  @Test
  @DisplayName("도서 등록 (필수 필드만) - 성공")
  void createBookWithoutIsbn_Success() {
    // Given
    BookCreateCommand command = BookCreateCommand.builder()
        .title("필수 필드 도서")
        .author("필수 필드 저자")
        .publisher("필수 필드 출판사")
        .publishedDate(LocalDate.of(2024, 1, 1))
        .build();

    Book bookToSave = BookFixture.createBook();
    when(bookMapper.toEntity(command)).thenReturn(bookToSave);

    Book savedBook = BookFixture.createBookWithId(1L);
    when(bookRepository.save(any(Book.class))).thenReturn(savedBook);

    BookResponse expectedResponse = BookResponse.builder()
        .id(1L)
        .title(command.getTitle())
        .author(command.getAuthor())
        .publisher(command.getPublisher())
        .publishedDate(command.getPublishedDate())
        .reviewCount(0)
        .rating(BigDecimal.ZERO)
        .build();
    when(bookMapper.toBookResponse(any(Book.class))).thenReturn(expectedResponse);

    // When
    BookResponse response = bookService.createBook(command);

    // Then
    assertThat(response).isNotNull();
    assertThat(response.getIsbn()).isNull();

    verify(bookRepository, never()).findByIsbn(any());
    verify(bookMapper).toEntity(command);
    verify(bookRepository).save(any(Book.class));
    verify(bookMapper).toBookResponse(any(Book.class));
  }

  @Test
  @DisplayName("도서 등록 - 실패 (중복된 ISBN)")
  void createBook_DuplicateIsbn() {
    // Given
    BookCreateCommand command = BookCreateCommand.builder()
        .title("중복 ISBN 도서")
        .author("중복 ISBN 저자")
        .publisher("중복 ISBN 출판사")
        .publishedDate(LocalDate.of(2024, 1, 1))
        .isbn("1234567890123")
        .build();

    Book existingBook = BookFixture.createBookWithIsbn("1234567890123");
    when(bookRepository.findByIsbn(command.getIsbn())).thenReturn(Optional.of(existingBook));

    // When & Then
    assertThatThrownBy(() -> bookService.createBook(command))
        .isInstanceOf(CustomException.class)
        .hasFieldOrPropertyWithValue("errorCode", ErrorCode.DUPLICATE_ISBN.getCode());

    verify(bookRepository).findByIsbn(command.getIsbn());
    verify(bookMapper, never()).toEntity(any());
    verify(bookRepository, never()).save(any());
  }
}