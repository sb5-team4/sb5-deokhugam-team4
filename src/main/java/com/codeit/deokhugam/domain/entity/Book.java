package com.codeit.deokhugam.domain.entity;

import com.codeit.deokhugam.domain.entity.base.BaseUpdatableEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "book")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Book extends BaseUpdatableEntity {

  @Column(name = "title", nullable = false, length = 255)
  private String title;
  @Column(name = "author", nullable = false, length = 255)
  private String author;
  @Column(name = "description", columnDefinition = "TEXT")
  private String description;
  @Column(name = "publisher", nullable = false, length = 255)
  private String publisher;
  @Column(name = "published_date", nullable = false)
  private LocalDate publishedDate;
  @Column(name = "isbn", unique = true, length = 13)
  private String isbn;
  @Column(name = "thumbnail_url", length = 500)
  private String thumbnailUrl;
  @Column(name = "review_count", nullable = false)
  private int reviewCount = 0;
  @Column(name = "rating", nullable = false, precision = 3, scale = 2)
  private BigDecimal rating = BigDecimal.ZERO;
  @Column(name = "deleted", nullable = false)
  private boolean deleted = false;

}
