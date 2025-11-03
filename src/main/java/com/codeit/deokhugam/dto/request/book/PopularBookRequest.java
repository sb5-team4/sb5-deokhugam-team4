package com.codeit.deokhugam.dto.request.book;

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
public class PopularBookRequest {

  @Pattern(regexp = "DAILY|WEEKLY|MONTHLY|ALL_TIME", message = "period는 DAILY, WEEKLY, MONTHLY, ALL_TIME 중 하나여야 합니다.")
  @Builder.Default
  private String period = "DAILY";

  @Pattern(regexp = "ASC|DESC", message = "direction은 ASC 또는 DESC여야 합니다.")
  @Builder.Default
  private String direction = "ASC";

  private String cursor;
  private Instant after;
  private Integer limit = 50;

}
