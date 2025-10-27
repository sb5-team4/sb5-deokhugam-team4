package com.codeit.deokhugam.domain.enums;


public enum Period {
  DAILY, WEEKLY, MONTHLY, ALL_TIME;


  public static Period from(String value) {
    if (value == null) {
      return null;
    }
    try {
      return Period.valueOf(value.toUpperCase());
    } catch (IllegalArgumentException e) {
      throw new RuntimeException("Unknown period: " + value);
    }
  }
}
