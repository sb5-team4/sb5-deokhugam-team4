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
public class PopularBookCommand {

  private String period;
  private String direction;
  private String cursor;
  private Instant after;
  private Integer limit;

}
