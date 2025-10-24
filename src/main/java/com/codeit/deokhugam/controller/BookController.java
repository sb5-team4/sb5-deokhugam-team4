package com.codeit.deokhugam.controller;

import com.codeit.deokhugam.dto.command.BookUpdateCommand;
import com.codeit.deokhugam.dto.request.BookUpdateRequest;
import com.codeit.deokhugam.dto.response.BookResponse;
import com.codeit.deokhugam.dto.result.BookUpdateResult;
import com.codeit.deokhugam.mapper.BookMapper;
import com.codeit.deokhugam.service.BookService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/books")
@RequiredArgsConstructor
public class BookController {

  private final BookService bookService;
  private final BookMapper bookMapper;

  @GetMapping("/{bookId}")
  public ResponseEntity<BookResponse> getBook(@PathVariable Long bookId) {
    BookResponse response = bookService.getBook(bookId);
    return ResponseEntity.ok(response);
  }

  @PatchMapping("/{bookId}")
  public ResponseEntity<BookResponse> updateBook(
      @PathVariable Long bookId,
      @RequestPart("bookData") @Valid BookUpdateRequest request,
      @RequestPart(value = "thumbnailImage", required = false) MultipartFile thumbnailImage) {

    BookUpdateCommand command = bookMapper.toBookUpdateCommand(request);
    BookUpdateResult result = bookService.updateBook(bookId, command, thumbnailImage);
    BookResponse response = bookMapper.toBookResponse(result);

    return ResponseEntity.ok(response);
  }

  @DeleteMapping("/{bookId}")
  public ResponseEntity<Void> softDeleteBook(@PathVariable Long bookId) {
    bookService.softDeleteBook(bookId);
    return ResponseEntity.noContent().build();
  }

  @DeleteMapping("/{bookId}/hard")
  public ResponseEntity<Void> hardDeleteBook(@PathVariable Long bookId) {
    bookService.hardDeleteBook(bookId);
    return ResponseEntity.noContent().build();
  }
}