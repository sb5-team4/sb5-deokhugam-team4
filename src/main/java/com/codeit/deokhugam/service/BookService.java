package com.codeit.deokhugam.service;


import com.codeit.deokhugam.domain.entity.Book;
import com.codeit.deokhugam.dto.response.BookResponse;
import com.codeit.deokhugam.repository.BookRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BookService {

  private final BookRepository bookRepository;

  public BookResponse getBook(Long id) {
    Book book = bookRepository.findById(id)
        .orElseThrow(() -> new RuntimeException("해당하는 도서 ID가 존재하지 않습니다: " + id));

    return BookResponse.from(book);
  }

}