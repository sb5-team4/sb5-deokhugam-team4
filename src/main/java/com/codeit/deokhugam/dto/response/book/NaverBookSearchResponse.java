package com.codeit.deokhugam.dto.response.book;

import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class NaverBookSearchResponse {

  private String lastBuildDate;  // 검색 결과를 생성한 시간
  private Integer total;         // 총 검색 결과 개수
  private Integer start;         // 검색 시작 위치
  private Integer display;       // 한 번에 표시할 검색 결과 개수
  private List<Item> items;      // 검색 결과 목록


  // Naver API 에서 땡겨오는 도서 상세 정보 필드들
  @Getter
  @NoArgsConstructor
  public static class Item {

    private String title;       // 도서 제목
    private String link;        // 네이버 도서 정보 URL
    private String image;       // 썸네일 이미지 URL
    private String author;      // 저자 정보
    private String discount;    // 할인된 가격
    private String publisher;   // 출판사
    private String pubdate;     // 출판일
    private String isbn;        // ISBN (ISBN10 ISBN13 형식)
    private String description; // 도서 소개
  }
}