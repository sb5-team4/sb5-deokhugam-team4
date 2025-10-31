package com.codeit.deokhugam.cache;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class InMemoryDebounceCache implements DebounceCache {

  private final Map<String, Long> cache = new ConcurrentHashMap<>();

  /**
   * 지정한 key에 대해 일정 시간(ttlMs) 내 중복 호출을 막기 위한 메서드. <br/> - 캐시에 해당 key가 없거나 TTL이 만료된 경우 → true 반환 (처리
   * 허용) → 현재시각으로 갱신<br/> - TTL 내에 동일 key 요청이 이미 존재한다면 → false 반환 (처리 차단) 예: 좋아요 이벤트 중복 방지용
   */
  @Override
  public boolean tryAcquire(String key, long ttlMs) {
    long now = System.currentTimeMillis();
    Long last = cache.get(key);

    if (last == null || now - last > ttlMs) {
      cache.put(key, now);
      return true;
    }
    return false;
  }
}
