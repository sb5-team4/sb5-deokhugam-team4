package com.codeit.deokhugam.domain.entity;

import com.codeit.deokhugam.domain.entity.base.BaseUpdatableEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
public class Book extends BaseUpdatableEntity {

  @Column(name = "title", nullable = false)
  private String title;
  @Column(name = "author", nullable = false)
  private String author;
  @Column(name = "description", nullable = false)
  private String description;
  @Column(name = "publisher", nullable = false)
  private String publisher;
  @Column(name = "published_date", nullable = false)
  private LocalDate published_date;
  @Column(name = "review_count", nullable = false)
  private int review_count;
  @Column(name = "rating", nullable = false)
  private BigDecimal rating;
  @Column(name = "deleted", nullable = false)
  private boolean deleted;

  private String isbn;
  private String thumbnail_url;

}
