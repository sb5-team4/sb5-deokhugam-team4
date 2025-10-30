package com.codeit.deokhugam.controller;

import com.codeit.deokhugam.dto.command.BookCreateCommand;
import com.codeit.deokhugam.dto.command.BookInfoByIsbnCommand;
import com.codeit.deokhugam.dto.command.BookListCommand;
import com.codeit.deokhugam.dto.command.BookUpdateCommand;
import com.codeit.deokhugam.dto.request.BookCreateRequest;
import com.codeit.deokhugam.dto.request.BookListRequest;
import com.codeit.deokhugam.dto.request.BookUpdateRequest;
import com.codeit.deokhugam.dto.response.BookListResponse;
import com.codeit.deokhugam.dto.response.BookResponse;
import com.codeit.deokhugam.dto.response.NaverBookResponse;
import com.codeit.deokhugam.dto.result.BookCreateResult;
import com.codeit.deokhugam.dto.result.BookInfoByIsbnResult;
import com.codeit.deokhugam.dto.result.BookListResult;
import com.codeit.deokhugam.dto.result.BookUpdateResult;
import com.codeit.deokhugam.mapper.BookMapper;
import com.codeit.deokhugam.service.BookService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/books")
@RequiredArgsConstructor
public class BookController {

  private final BookService bookService;
  private final BookMapper bookMapper;

  /**
   * 도서 등록
   *
   * @param bookData
   * @param thumbnailImage
   * @return ResponseEntity<BookResponse>
   */
  @PostMapping
  public ResponseEntity<BookResponse> createBook(
      @RequestPart(value = "bookData", required = true) BookCreateRequest bookData,
      @RequestPart(value = "BookData", required = false) BookCreateRequest bookDataAlt,  // 대문자도 허용
      @RequestPart(value = "thumbnailImage", required = false) MultipartFile thumbnailImage
  ) {
    BookCreateCommand command = bookMapper.toBookCreateCommand(bookData);
    BookCreateResult result = bookService.createBook(command, thumbnailImage);
    BookResponse response = bookMapper.toBookResponse(result);
    return ResponseEntity.status(HttpStatus.CREATED).body(response);
  }

  /**
   * 도서 ID로 도서 정보 조회
   *
   * @param bookId
   * @return ResponseEntity<BookResponse>
   */
  @GetMapping("/{bookId}")
  public ResponseEntity<BookResponse> getBook(@PathVariable Long bookId) {
    BookResponse response = bookService.getBook(bookId);
    return ResponseEntity.ok(response);
  }

  /**
   * 도서 ID로 도서 정보 수정
   *
   * @param bookId
   * @param request
   * @param thumbnailImage
   * @return ResponseEntity<BookResponse>
   */
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

  /**
   * 도서 논리 삭제
   *
   * @param bookId
   * @return ResponseEntity<Void>
   */
  @DeleteMapping("/{bookId}")
  public ResponseEntity<Void> softDeleteBook(@PathVariable Long bookId) {
    bookService.softDeleteBook(bookId);
    return ResponseEntity.noContent().build();
  }

  /**
   * 도서 물리 삭제
   *
   * @param bookId
   * @return ResponseEntity<Void>
   */
  @DeleteMapping("/{bookId}/hard")
  public ResponseEntity<Void> hardDeleteBook(@PathVariable Long bookId) {
    bookService.hardDeleteBook(bookId);
    return ResponseEntity.noContent().build();
  }

  /**
   * ISBN으로 Naver API의 도서 상세 정보 조회
   *
   * @param isbn
   * @return ResponseEntity<NaverBookResponse>
   */
  @GetMapping("/info")
  public ResponseEntity<NaverBookResponse> getBookInfoByIsbn(
      @RequestParam @NotBlank(message = "ISBN을 입력해주세요.") String isbn) {
    BookInfoByIsbnCommand command = BookInfoByIsbnCommand.builder()
        .isbn(isbn)
        .build();

    BookInfoByIsbnResult result = bookService.getBookInfoByIsbn(command);

    NaverBookResponse response = bookMapper.toNaverBookResponse(result);

    return ResponseEntity.ok(response);
  }

  /**
   * 도서 목록 조회
   *
   * @param 검색/정렬/페이지네이션 조건
   * @return 도서 목록
   */
  @GetMapping
  public ResponseEntity<BookListResponse> getBookList(
      @Valid @ModelAttribute BookListRequest request
  ) {
    BookListCommand command = bookMapper.toBookListCommand(request);
    BookListResult result = bookService.getBookList(command);
    BookListResponse response = bookMapper.toBookListResponse(result);
    return ResponseEntity.ok(response);
  }
}