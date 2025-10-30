package com.codeit.deokhugam.dto.command;


import java.time.Instant;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookListCommand {

  private String keyword;   // 검색 키워드
  private String orderBy;   // 정렬 기준
  private String direction; // 정렬 방향
  private String cursor;    // 커서 페이지네이션 커서
  private Instant after;    // 보조 커서 (createdAt)
  private Integer limit;    // 페이지 크기 (defalt = 50)

}
