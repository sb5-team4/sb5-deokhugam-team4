package com.codeit.deokhugam.service.book;

import com.codeit.deokhugam.common.exception.handler.CustomException;
import com.codeit.deokhugam.common.exception.handler.ErrorCode;
import com.codeit.deokhugam.dto.response.book.NaverBookSearchResponse;
import com.codeit.deokhugam.dto.result.book.BookInfoByIsbnResult;
import com.codeit.deokhugam.mapper.book.BookMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Service
@RequiredArgsConstructor
public class NaverApiService {

  private final RestTemplate restTemplate;
  private final BookMapper bookMapper;

  @Value("${naver.api.client-id}")
  private String clientId;

  @Value("${naver.api.client-secret}")
  private String clientSecret;

  private static final String NAVER_BOOK_API_URL = "https://openapi.naver.com/v1/search/book.json";


  public BookInfoByIsbnResult getBookByIsbn(String isbn) {
    String url = UriComponentsBuilder.fromHttpUrl(NAVER_BOOK_API_URL)
        .queryParam("query", isbn)
        .queryParam("display", 1)
        .toUriString();

    HttpHeaders headers = new HttpHeaders();
    headers.set("X-Naver-Client-Id", clientId);
    headers.set("X-Naver-Client-Secret", clientSecret);

    HttpEntity<String> entity = new HttpEntity<>(headers);

    ResponseEntity<NaverBookSearchResponse> response = restTemplate.exchange(
        url,
        HttpMethod.GET,
        entity,
        NaverBookSearchResponse.class
    );

    NaverBookSearchResponse responseBody = response.getBody();
    if (responseBody == null || responseBody.getItems() == null
        || responseBody.getItems().isEmpty()) {
      throw new CustomException(ErrorCode.BOOK_ISBN_NOT_FOUND);
    }

    NaverBookSearchResponse.Item item = responseBody.getItems().get(0);
    return bookMapper.toBookInfoByIsbnResult(item);
  }

}
