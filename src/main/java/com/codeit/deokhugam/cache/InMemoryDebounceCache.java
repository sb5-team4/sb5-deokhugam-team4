package com.codeit.deokhugam.cache;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Component;

@Component
public class InMemoryDebounceCache implements DebounceCache {

  private final Map<String, Long> cache = new ConcurrentHashMap<>();

  @Override
  public boolean tryAcquire(String key, long ttlMs) {

    return false;
  }
}
