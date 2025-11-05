package com.codeit.deokhugam.domain.entity;

import com.codeit.deokhugam.domain.entity.base.BaseUpdatableEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "book")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class Book extends BaseUpdatableEntity {

  @Column(name = "title", nullable = false, length = 255)
  private String title;
  @Column(name = "author", nullable = false, length = 255)
  private String author;
  @Column(name = "description", nullable = false, columnDefinition = "TEXT", length = 1000)
  private String description;
  @Column(name = "publisher", nullable = false, length = 255)
  private String publisher;
  @Column(name = "published_date", nullable = false)
  private LocalDate publishedDate;
  @Column(name = "isbn", unique = true, length = 13)
  private String isbn;
  @Column(name = "thumbnail_url", length = 500)
  private String thumbnailUrl;

  @Builder.Default
  @Column(name = "review_count", nullable = false)
  private Long reviewCount = 0L;

  @Builder.Default
  @Column(name = "rating", nullable = false, precision = 3, scale = 2)
  private BigDecimal rating = BigDecimal.ZERO;

  @Builder.Default
  @Column(name = "deleted", nullable = false)
  private boolean deleted = false;

  public void registerReview(Review review) {
    BigDecimal newRating = BigDecimal.valueOf(review.getRating());
    BigDecimal total = this.rating.multiply(BigDecimal.valueOf(this.reviewCount));

    this.reviewCount += 1;
    this.rating = total.add(newRating)
        .divide(BigDecimal.valueOf(this.reviewCount), 2, RoundingMode.HALF_UP);
  }

  public void removeReview(Review review) {
    if (this.reviewCount == 0) {
      return;
    }

    BigDecimal removedRating = BigDecimal.valueOf(review.getRating());
    BigDecimal total = this.rating.multiply(BigDecimal.valueOf(this.reviewCount));

    this.reviewCount -= 1;
    if (this.reviewCount == 0) {
      this.rating = BigDecimal.ZERO;
    } else {
      this.rating = total.subtract(removedRating)
          .divide(BigDecimal.valueOf(this.reviewCount), 2, RoundingMode.HALF_UP);
    }
  }


}
