package com.codeit.deokhugam.service;

import com.codeit.deokhugam.domain.entity.Book;
import com.codeit.deokhugam.dto.command.BookUpdateCommand;
import com.codeit.deokhugam.dto.response.BookResponse;
import com.codeit.deokhugam.dto.result.BookUpdateResult;
import com.codeit.deokhugam.mapper.BookMapper;
import com.codeit.deokhugam.repository.BookRepository;
import java.util.NoSuchElementException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class BookService {

  private final BookRepository bookRepository;
  private final BookMapper bookMapper;  // MapStruct가 생성한 구현체 자동 주입
  private final S3Service s3Service;

  public BookResponse getBook(Long id) {
    Book book = bookRepository.findById(id)
        .orElseThrow(() -> new NoSuchElementException("해당하는 도서 ID가 존재하지 않습니다: " + id));

    if (book.isDeleted()) {
      throw new NoSuchElementException("이미 삭제된 도서입니다: " + id);
    }

    return bookMapper.toBookResponse(book);
  }

  @Transactional
  public BookUpdateResult updateBook(Long id, BookUpdateCommand command,
      MultipartFile thumbnailImage) {
    Book book = bookRepository.findById(id)
        .orElseThrow(() -> new NoSuchElementException("해당하는 도서 ID가 존재하지 않습니다: " + id));

    if (book.isDeleted()) {
      throw new IllegalStateException("삭제된 도서는 수정할 수 없습니다.");
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
        .orElseThrow(() -> new NoSuchElementException("해당하는 도서 ID가 존재하지 않습니다: " + id));

    if (!book.isDeleted()) {
      book.setDeleted(true);
      bookRepository.save(book);
    }
  }

  @Transactional
  public void hardDeleteBook(Long id) {
    Book book = bookRepository.findById(id)
        .orElseThrow(() -> new NoSuchElementException("해당하는 도서 ID가 존재하지 않습니다: " + id));

    bookRepository.delete(book);
  }
}