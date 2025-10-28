package com.codeit.deokhugam.fixture;

import com.codeit.deokhugam.domain.entity.Member;
import java.util.concurrent.atomic.AtomicLong;

public class MemberFixture {

  private static final AtomicLong counter = new AtomicLong(1);

  private static String unique(String prefix) {
    return prefix + "_" + counter.getAndIncrement();
  }

  public static Member createDefaultMember() {
    return Member.builder()
        .email(unique("user") + "@test.com")
        .nickname(unique("닉네임"))
        .password("encoded-password")
        .deleted(false)
        .build();
  }

  public static Member createDeletedMember() {
    return createDefaultMember().toBuilder()
        .deleted(true)
        .build();
  }

}
