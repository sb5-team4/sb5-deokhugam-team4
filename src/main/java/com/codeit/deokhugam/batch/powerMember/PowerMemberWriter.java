package com.codeit.deokhugam.batch.powerMember;

import com.codeit.deokhugam.domain.entity.PowerMember;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class PowerMemberWriter implements ItemWriter<PowerMember> {

  private final PowerMemberRepository powerMemberRepository;

  @Override
  public void write(Chunk<? extends PowerMember> chunk) throws Exception {
    log.info("Writing {} PowerMembers", chunk.getItems().size());
    if (chunk != null && !chunk.isEmpty()) {
      powerMemberRepository.saveAll(chunk.getItems());
      powerMemberRepository.flush();

    }
  }
}
