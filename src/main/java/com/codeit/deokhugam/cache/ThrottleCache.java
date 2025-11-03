package com.codeit.deokhugam.cache;

public interface ThrottleCache {

  boolean tryAcquire(String key, long ttlMs);
}
