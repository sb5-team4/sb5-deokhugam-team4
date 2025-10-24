package com.codeit.deokhugam.controller;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.codeit.deokhugam.dto.response.BookResponse;
import com.codeit.deokhugam.mapper.BookMapper;
import com.codeit.deokhugam.service.BookService;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.NoSuchElementException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(controllers = BookController.class)
class BookControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @MockitoBean
  private BookService bookService;
  @MockitoBean
  private BookMapper bookMapper;

  @MockitoBean
  private org.springframework.data.jpa.mapping.JpaMetamodelMappingContext jpaMetamodelMappingContext;

  @Test
  @DisplayName("도서 ID로 상세 정보 조회 - 성공")
  void getBook_Success() throws Exception {
    // given
    Long bookId = 1L;
    BookResponse mockResponse = BookResponse.builder()
        .id(bookId)
        .title("테스트 도서")
        .author("테스트 저자")
        .description("테스트 설명")
        .publisher("테스트 출판사")
        .publishedDate(LocalDate.of(1990, 12, 30))
        .isbn("1234567890123")
        .thumbnailUrl("testThumbnailUrl.png")
        .reviewCount(5)
        .rating(new BigDecimal("2.31"))
        .createdAt(Instant.now())
        .updatedAt(Instant.now())
        .build();

    given(bookService.getBook(bookId)).willReturn(mockResponse);

    // when & then
    mockMvc.perform(get("/api/books/{bookId}", bookId))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(bookId))
        .andExpect(jsonPath("$.title").value("테스트 도서"))
        .andExpect(jsonPath("$.author").value("테스트 저자"))
        .andExpect(jsonPath("$.description").value("테스트 설명"))
        .andExpect(jsonPath("$.publisher").value("테스트 출판사"))
        .andExpect(jsonPath("$.publishedDate").value("1990-12-30"))
        .andExpect(jsonPath("$.isbn").value("1234567890123"))
        .andExpect(jsonPath("$.thumbnailUrl").value("testThumbnailUrl.png"))
        .andExpect(jsonPath("$.reviewCount").value(5))
        .andExpect(jsonPath("$.rating").value(2.31))
        .andExpect(jsonPath("$.createdAt").exists())
        .andExpect(jsonPath("$.updatedAt").exists());
  }

  @Test
  @DisplayName("조회할 도서 ID 정보 없음 - 404 에러 반환")
  void getBook_NotFound() throws Exception {
    // given
    Long nonExistentBookId = 99999L;

    given(bookService.getBook(nonExistentBookId))
        .willThrow(new NoSuchElementException("해당하는 도서 ID가 존재하지 않습니다: " + nonExistentBookId));

    // when & then
    mockMvc.perform(get("/api/books/{bookId}", nonExistentBookId))
        .andDo(print())
        .andExpect(status().isNotFound());
  }
}