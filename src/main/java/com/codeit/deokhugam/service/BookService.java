package com.codeit.deokhugam.service;


import com.codeit.deokhugam.domain.entity.Book;
import com.codeit.deokhugam.dto.command.BookUpdateCommand;
import com.codeit.deokhugam.dto.request.BookUpdateRequest;
import com.codeit.deokhugam.dto.response.BookResponse;
import com.codeit.deokhugam.mapper.BookMapper;
import com.codeit.deokhugam.repository.BookRepository;
import jakarta.transaction.Transactional;
import java.util.NoSuchElementException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class BookService {

  private final BookRepository bookRepository;
  private final BookMapper bookMapper;
  private final S3Service s3Service; // 인터페이스 주입

  public BookResponse getBook(Long id) {
    Book book = bookRepository.findById(id)
        .orElseThrow(() -> new NoSuchElementException("해당하는 도서 ID가 존재하지 않습니다: " + id));

    return BookResponse.from(book);
  }

  @Transactional
  public BookResponse updateBook(Long id, BookUpdateRequest request, MultipartFile thumbnailImage) {
    Book book = bookRepository.findById(id)
        .orElseThrow(() -> new NoSuchElementException("해당하는 도서 ID가 존재하지 않습니다: " + id));

    if (book.isDeleted()) {
      throw new IllegalStateException("삭제된 도서는 수정할 수 없습니다.");
    }

    BookUpdateCommand command = bookMapper.toBookUpdateCommand(request);
    bookMapper.updateBookFromCommand(command, book);

    if (thumbnailImage != null && !thumbnailImage.isEmpty()) {
      String uploadedUrl = s3Service.uploadFile(thumbnailImage);
      book.setThumbnailUrl(uploadedUrl);
    }

    Book updatedBook = bookRepository.save(book);
    return BookResponse.from(updatedBook);
  }
}