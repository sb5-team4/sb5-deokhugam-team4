package com.codeit.deokhugam.controller.book;

import com.codeit.deokhugam.dto.command.book.PopularBookCommand;
import com.codeit.deokhugam.dto.request.book.PopularBookRequest;
import com.codeit.deokhugam.dto.response.book.PopularBookListResponse;
import com.codeit.deokhugam.dto.response.book.PopularBookListResponse.PopularBookResponse;
import com.codeit.deokhugam.dto.result.book.PopularBookListResult;
import com.codeit.deokhugam.dto.result.book.PopularBookListResult.PopularBookResult;
import com.codeit.deokhugam.service.book.PopularBookService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/books")
@RequiredArgsConstructor
public class PopularBookController {

  private final PopularBookService popularBookService;

  /**
   * 인기 도서 목록 조회 API
   *
   * @param request
   * @return 인기 도서 목록 응답
   */
  @GetMapping("/popular")
  public ResponseEntity<PopularBookListResponse> getPopularBooks(
      @Valid @ModelAttribute PopularBookRequest request
  ) {
    PopularBookCommand command = PopularBookCommand.builder()
        .period(request.getPeriod())
        .direction(request.getDirection())
        .cursor(request.getCursor())
        .after(request.getAfter())
        .limit(request.getLimit())
        .build();

    PopularBookListResult result = popularBookService.getPopularBooks(command);

    List<PopularBookResponse> content = result.getContent().stream()
        .map(this::toPopularBookResponse)
        .toList();

    PopularBookListResponse response = PopularBookListResponse.builder()
        .content(content)
        .nextCursor(result.getNextCursor())
        .nextAfter(result.getNextAfter())
        .size(result.getSize())
        .totalElements(result.getTotalElements())
        .hasNext(result.getHasNext())
        .build();

    return ResponseEntity.ok(response);
  }

  /**
   * PopularBookResult를 PopularBookResponse로 변환
   *
   * @param result PopularBookResult
   * @return PopularBookResponse
   */
  private PopularBookResponse toPopularBookResponse(PopularBookResult result) {
    return PopularBookResponse.builder()
        .id(result.getId())
        .bookId(result.getBookId())
        .title(result.getTitle())
        .author(result.getAuthor())
        .thumbnailUrl(result.getThumbnailUrl())
        .period(result.getPeriod())
        .rank(result.getRank())
        .score(result.getScore())
        .reviewCount(result.getReviewCount())
        .rating(result.getRating())
        .createdAt(result.getCreatedAt())
        .build();
  }
}
