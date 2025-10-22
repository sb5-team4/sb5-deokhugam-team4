package com.codeit.deokhugam.controller;

import com.codeit.deokhugam.dto.response.BookResponse;
import com.codeit.deokhugam.repository.BookRepository;
import com.codeit.deokhugam.service.BookService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/books")
@RequiredArgsConstructor
public class BookController {

  private final BookService bookService;
  private final BookRepository bookRepository;

  @GetMapping("/{bookId}")
  public ResponseEntity<BookResponse> getBook(@PathVariable Long bookId) {
    BookResponse response = bookService.getBook(bookId);
    return ResponseEntity.ok(response);
  }

}
