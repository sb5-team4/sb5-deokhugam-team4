package com.codeit.deokhugam.service;

import com.codeit.deokhugam.common.exception.handler.CustomException;
import com.codeit.deokhugam.common.exception.handler.ErrorCode;
import com.codeit.deokhugam.domain.entity.Book;
import com.codeit.deokhugam.dto.command.BookCreateCommand;
import com.codeit.deokhugam.dto.command.BookUpdateCommand;
import com.codeit.deokhugam.dto.response.BookResponse;
import com.codeit.deokhugam.dto.result.BookUpdateResult;
import com.codeit.deokhugam.mapper.BookMapper;
import com.codeit.deokhugam.repository.BookRepository;
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

  public BookResponse createBook(BookCreateCommand command) {
    if (command.getIsbn() != null && !command.getIsbn().isBlank()) {
      bookRepository.findByIsbn(command.getIsbn())
          .ifPresent(book -> {
            throw new CustomException(ErrorCode.DUPLICATE_ISBN);
          });
    }
    Book book = bookMapper.toEntity(command);
    Book savedBook = bookRepository.save(book);

    return bookMapper.toBookResponse(savedBook);
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

    if (thumbnailImage != null && !thumbnailImage.isEmpty()) {
      String uploadedUrl = s3Service.uploadFile(thumbnailImage);
      book.setThumbnailUrl(uploadedUrl);
    }

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

    bookRepository.delete(book);
  }
}