package com.codeit.deokhugam.batch.popularReview;

import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.Setter;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.stereotype.Component;

@Component
@JobScope
@Getter
@Setter
public class RankCounter {

  private long currentRank;

  @PostConstruct
  public void init() {
    this.currentRank = Long.MAX_VALUE;
  }

  public long nextRank() {
    return currentRank--;
  }
}
