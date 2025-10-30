package com.codeit.deokhugam.dto.request;

import jakarta.validation.constraints.Pattern;
import java.time.Instant;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookListRequest {

  private String keyword;             // 검색 키워드

  @Pattern(regexp = "^(title|publishedDate|rating|reviewCount)$")
  @Builder.Default
  private String orderBy = "title";   // 정렬 기준

  @Pattern(regexp = "^(ASC|DESC)$")
  @Builder.Default
  private String direction = "DESC";  // 정렬 방향

  private String cursor;              // 커서 페이지네이션 커서
  private Instant after;              // 보조 커서 (createdAt)
  private Integer limit = 50;         // 페이지 크기 (default = 50)

}
