package com.codeit.deokhugam.cache;

public interface DebounceCache {

  boolean tryAcquire(String key, long ttlMs);
}
