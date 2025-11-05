package com.codeit.deokhugam.batch.powerMember;

import com.codeit.deokhugam.batch.powerMember.dto.PowerMemberScoreDto;
import com.codeit.deokhugam.repository.ReviewRepository;
import java.time.DayOfWeek;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemReader;

@Slf4j
@RequiredArgsConstructor
@StepScope
public class PowerMemberReader implements ItemReader<PowerMemberScoreDto> {

  private final ReviewRepository reviewRepository;
  private Iterator<PowerMemberScoreDto> iterator;

  @Override
  public PowerMemberScoreDto read() {
    // iterator가 아직 초기화되지 않았으면 초기화
    if (iterator == null) {
      List<PowerMemberScoreDto> allDtos = new ArrayList<>();
      Instant now = Instant.now();

      Map<String, Instant[]> periods = Map.of(
          "DAILY",
          new Instant[]{LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant(), now},
          "WEEKLY", new Instant[]{
              LocalDate.now().with(DayOfWeek.MONDAY)
                  .atStartOfDay(ZoneId.systemDefault()).toInstant(),
              now},
          "MONTHLY", new Instant[]{
              LocalDate.now().withDayOfMonth(1).atStartOfDay(ZoneId.systemDefault()).toInstant(),
              now},
          "ALL_TIME",
          new Instant[]{
              LocalDateTime.of(1970, 1, 1, 0, 0).atZone(ZoneId.systemDefault()).toInstant(),
              now}
      );

      for (Map.Entry<String, Instant[]> entry : periods.entrySet()) {
        String period = entry.getKey();
        Instant start = entry.getValue()[0];
        Instant end = entry.getValue()[1];

        List<PowerMemberScoreDto> dtos = reviewRepository.findPowerMemberScoreDto(start, end);
        dtos.forEach(dto -> dto.setPeriod(period));
        allDtos.addAll(dtos);
      }

      iterator = allDtos.iterator();
    }

    if (iterator.hasNext()) {
      return iterator.next();
    }
    return null;
  }
}
