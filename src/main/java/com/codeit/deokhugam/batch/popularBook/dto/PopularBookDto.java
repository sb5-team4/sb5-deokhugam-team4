package com.codeit.deokhugam.batch.popularBook.dto;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PopularBookDto {

  private Long bookId;              // 도서 ID
  private String title;             // 도서 제목
  private String author;            // 저자
  private String thumbnailUrl;      // 썸네일 URL
  private Long reviewCount;         // 해당 기간의 리뷰 수
  private BigDecimal averageRating; // 해당 기간의 평균 평점
  private String period;            // 기간 (DAILY, WEEKLY, MONTHLY, ALL_TIME)


  public PopularBookDto(Long bookId, String title, String author,
      String thumbnailUrl, Long reviewCount, Double averageRating) {
    this.bookId = bookId;
    this.title = title;
    this.author = author;
    this.thumbnailUrl = thumbnailUrl;
    this.reviewCount = reviewCount;
    // Double을 BigDecimal로 변환 (null이면 0으로 처리)
    this.averageRating = (averageRating != null)
        ? BigDecimal.valueOf(averageRating)
        : BigDecimal.ZERO;
  }

  // 점수 계산식: (리뷰수 * 0.4) + (평균평점 * 0.6)
  public BigDecimal calculateScore() {
    if (reviewCount == null || reviewCount == 0) {
      return BigDecimal.ZERO;
    }

    BigDecimal rating = (averageRating != null) ? averageRating : BigDecimal.ZERO;

    BigDecimal reviewScore = BigDecimal.valueOf(reviewCount)
        .multiply(BigDecimal.valueOf(0.4));
    BigDecimal ratingScore = rating.multiply(BigDecimal.valueOf(0.6));

    return reviewScore.add(ratingScore);
  }
}