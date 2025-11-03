package com.codeit.deokhugam.service.book;

import com.codeit.deokhugam.common.exception.handler.CustomException;
import com.codeit.deokhugam.common.exception.handler.ErrorCode;
import com.codeit.deokhugam.domain.entity.Book;
import com.codeit.deokhugam.dto.command.book.BookCreateCommand;
import com.codeit.deokhugam.dto.command.book.BookInfoByIsbnCommand;
import com.codeit.deokhugam.dto.command.book.BookListCommand;
import com.codeit.deokhugam.dto.command.book.BookUpdateCommand;
import com.codeit.deokhugam.dto.command.book.IsbnOcrCommand;
import com.codeit.deokhugam.dto.response.book.BookResponse;
import com.codeit.deokhugam.dto.result.book.BookCreateResult;
import com.codeit.deokhugam.dto.result.book.BookInfoByIsbnResult;
import com.codeit.deokhugam.dto.result.book.BookListResult;
import com.codeit.deokhugam.dto.result.book.BookUpdateResult;
import com.codeit.deokhugam.dto.result.book.IsbnOcrResult;
import com.codeit.deokhugam.mapper.book.BookMapper;
import com.codeit.deokhugam.repository.book.BookRepository;
import com.codeit.deokhugam.repository.book.impl.BookQueryRepositoryImpl;
import java.time.Instant;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class BookService {

  private final BookRepository bookRepository;
  private final BookMapper bookMapper;
  private final S3Service s3Service;
  private final NaverApiService naverApiService;
  private final BookQueryRepositoryImpl bookQueryRepository;
  private final IsbnOcrService isbnOcrService;

  @Transactional
  public BookCreateResult createBook(BookCreateCommand command, MultipartFile thumbnailImage) {
    if (command.getIsbn() != null && !command.getIsbn().isBlank()) {
      bookRepository.findByIsbn(command.getIsbn())
          .ifPresent(book -> {
            throw new CustomException(ErrorCode.DUPLICATE_ISBN);
          });
    }
    Book book = bookMapper.toEntity(command);

    if (thumbnailImage != null && !thumbnailImage.isEmpty()) {
      String url = s3Service.uploadFile(thumbnailImage);
      book.setThumbnailUrl(url);
    }

    Book savedBook = bookRepository.save(book);

    return bookMapper.toBookCreateResult(savedBook);
  }

  public BookResponse getBook(Long id) {
    Book book = bookRepository.findById(id)
        .orElseThrow(() -> new CustomException(ErrorCode.BOOK_NOT_FOUND, id));

    if (book.isDeleted()) {
      throw new CustomException(ErrorCode.BOOK_ALREADY_DELETED, id);
    }

    return bookMapper.toBookResponse(book);
  }

  @Transactional
  public BookUpdateResult updateBook(Long id, BookUpdateCommand command,
      MultipartFile thumbnailImage) {
    Book book = bookRepository.findById(id)
        .orElseThrow(() -> new CustomException(ErrorCode.BOOK_NOT_FOUND, id));

    if (book.isDeleted()) {
      throw new CustomException(ErrorCode.BOOK_ALREADY_DELETED, id);
    }

    bookMapper.updateBookFromCommand(command, book);

    // S3 이미지 업데이트 로직 추가 (Early return으로)
    if (thumbnailImage == null || thumbnailImage.isEmpty()) {
      Book updatedBook = bookRepository.save(book);
      return bookMapper.toBookUpdateResult(updatedBook);
    }

    if (book.getThumbnailUrl() != null && !book.getThumbnailUrl().isEmpty()) {
      s3Service.deleteFile(book.getThumbnailUrl());
    }

    String uploadedUrl = s3Service.uploadFile(thumbnailImage);
    book.setThumbnailUrl(uploadedUrl);

    Book updatedBook = bookRepository.save(book);
    return bookMapper.toBookUpdateResult(updatedBook);
  }

  @Transactional
  public void softDeleteBook(Long id) {
    Book book = bookRepository.findById(id)
        .orElseThrow(() -> new CustomException(ErrorCode.BOOK_NOT_FOUND, id));

    if (!book.isDeleted()) {
      book.setDeleted(true);
      bookRepository.save(book);
    }
  }

  @Transactional
  public void hardDeleteBook(Long id) {
    Book book = bookRepository.findById(id)
        .orElseThrow(() -> new CustomException(ErrorCode.BOOK_NOT_FOUND, id));

    if (book.getThumbnailUrl() != null && !book.getThumbnailUrl().isEmpty()) {
      s3Service.deleteFile(book.getThumbnailUrl());
    }

    bookRepository.delete(book);
  }

  public BookInfoByIsbnResult getBookInfoByIsbn(BookInfoByIsbnCommand command) {
    String isbn = command.getIsbn();

    if (isbn == null || !isbn.matches("\\d{13}")) {
      throw new CustomException(ErrorCode.ISBN_NOT_COLLECT);
    }

    return naverApiService.getBookByIsbn(isbn);
  }

  @Transactional(readOnly = true)
  public BookListResult getBookList(BookListCommand command) {
    String cursor = command.getCursor();
    Instant after = command.getAfter();

    List<Book> books = bookQueryRepository.findBooksWithCursor(
        command.getKeyword(),
        command.getOrderBy(),
        command.getDirection(),
        cursor,
        after,
        command.getLimit()
    );

    boolean hasNext = books.size() > command.getLimit();

    List<Book> content = hasNext ? books.subList(0, command.getLimit()) : books;

    List<BookListResult.BookResult> bookResults = content.stream()
        .map(bookMapper::toBookResult).toList();

    String nextCursor = null;
    Instant nextAfter = null;

    if (hasNext && !content.isEmpty()) {
      Book lastBook = content.get(content.size() - 1);
      nextCursor = getCursorValue(lastBook, command.getOrderBy());
      nextAfter = lastBook.getCreatedAt();
    }

    long totalElements = bookQueryRepository.countBooksWithCursor(
        command.getKeyword()
    );

    return BookListResult.builder()
        .content(bookResults)
        .nextCursor(nextCursor)
        .nextAfter(nextAfter)
        .size(content.size())
        .totalElements(totalElements)
        .hasNext(hasNext)
        .build();
  }

  // 하드코딩되어 있는 값들을 없앨 수 있는지
  private String getCursorValue(Book book, String orderBy) {
    return switch (orderBy) {
      case "title" -> book.getTitle();
      case "publishedDate" -> book.getPublishedDate().toString();
      case "rating" -> book.getRating().toString();
      case "reviewCount" -> String.valueOf(book.getReviewCount());
      default -> book.getTitle();
    };

  }

  @Transactional(readOnly = true)
  public IsbnOcrResult recognizeIsbnByOcr(IsbnOcrCommand command) {
    String isbn = isbnOcrService.extractIsbnFromImage(command.getImage());

    return IsbnOcrResult.builder()
        .isbn(isbn)
        .build();
  }
}