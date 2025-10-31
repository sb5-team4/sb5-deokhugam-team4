package com.codeit.deokhugam.batch.popularReview;

import static com.codeit.deokhugam.domain.enums.Period.DAILY;
import static com.codeit.deokhugam.domain.enums.Period.MONTHLY;
import static com.codeit.deokhugam.domain.enums.Period.WEEKLY;

import com.codeit.deokhugam.domain.entity.PopularReview;
import com.codeit.deokhugam.domain.entity.Review;
import com.codeit.deokhugam.domain.enums.Period;
import com.codeit.deokhugam.repository.CommentRepository;
import com.codeit.deokhugam.repository.ReviewLikeRepository;
import jakarta.annotation.PostConstruct;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.DayOfWeek;
import java.time.Instant;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.TemporalAdjusters;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

@Component
@JobScope
@RequiredArgsConstructor
public class PopularReviewProcessor implements ItemProcessor<Review, List<PopularReview>> {


  // 점수 = (해당 기간의 좋아요 수 * 0.3) + (해당 기간의 댓글 수 * 0.7)
  private static final BigDecimal LIKE_WEIGHT = new BigDecimal("0.3");
  private static final BigDecimal COMMENT_WEIGHT = new BigDecimal("0.7");
  private static final int RESULT_SCALE = 2;
  private static final RoundingMode ROUNDING = RoundingMode.HALF_UP;

  private final ReviewLikeRepository reviewLikeRepository;
  private final CommentRepository commentRepository;

  private Instant jobStartTime;

  @PostConstruct
  public void init() {
    this.jobStartTime = Instant.now(); // Job 실행 시점에 초기화
  }

  @Override
  public List<PopularReview> process(Review item) {

    ZonedDateTime zdt = jobStartTime.atZone(ZoneId.of("Asia/Seoul"));

    // ===== 기간별 시작/끝 시점 계산 =====
    ZonedDateTime startOfYear = zdt.withDayOfYear(1).toLocalDate()
        .atStartOfDay(ZoneId.of("Asia/Seoul"));
    ZonedDateTime endOfYear = startOfYear.plusYears(1);

    ZonedDateTime startOfMonth = zdt.withDayOfMonth(1).toLocalDate()
        .atStartOfDay(ZoneId.of("Asia/Seoul"));
    ZonedDateTime endOfMonth = startOfMonth.plusMonths(1);

    ZonedDateTime startOfWeek = zdt.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
        .toLocalDate()
        .atStartOfDay(ZoneId.of("Asia/Seoul"));
    ZonedDateTime endOfWeek = zdt.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY))
        .toLocalDate()
        .atTime(LocalTime.MAX)
        .atZone(ZoneId.of("Asia/Seoul"));

    ZonedDateTime endOfDay = zdt;
    ZonedDateTime startOfDay = endOfDay.minusDays(1);

    long reviewId = item.getId();

    // ===== All-Time =====
    long totalLikes = reviewLikeRepository.countByReviewId(reviewId);
    long totalComments = commentRepository.countByReviewId(reviewId);
    BigDecimal scoreAllTime = getScore(totalLikes, totalComments);
    PopularReview reviewByAllTime = PopularReview.fromByScoreNonOrdered(scoreAllTime,
        Period.ALL_TIME, item);

    // ===== Monthly =====
    long monthlyLikes = reviewLikeRepository.countByReviewIdAndCreatedAtBetween(
        reviewId, startOfMonth.toInstant(), endOfMonth.toInstant());
    long monthlyComments = commentRepository.countByReviewIdAndCreatedAtBetween(
        reviewId, startOfMonth.toInstant(), endOfMonth.toInstant());
    BigDecimal scoreMonthly = getScore(monthlyLikes, monthlyComments);
    PopularReview reviewMonthly = PopularReview.fromByScoreNonOrdered(scoreMonthly, MONTHLY, item);

    // ===== Weekly =====
    long weeklyLikes = reviewLikeRepository.countByReviewIdAndCreatedAtBetween(
        reviewId, startOfWeek.toInstant(), endOfWeek.toInstant());
    long weeklyComments = commentRepository.countByReviewIdAndCreatedAtBetween(
        reviewId, startOfWeek.toInstant(), endOfWeek.toInstant());
    BigDecimal scoreWeekly = getScore(weeklyLikes, weeklyComments);
    PopularReview reviewWeekly = PopularReview.fromByScoreNonOrdered(scoreWeekly, WEEKLY, item);

    // ===== Daily =====
    long dailyLikes = reviewLikeRepository.countByReviewIdAndCreatedAtBetween(
        reviewId, startOfDay.toInstant(), endOfDay.toInstant());
    long dailyComments = commentRepository.countByReviewIdAndCreatedAtBetween(
        reviewId, startOfDay.toInstant(), endOfDay.toInstant());
    BigDecimal scoreDaily = getScore(dailyLikes, dailyComments);
    PopularReview reviewDaily = PopularReview.fromByScoreNonOrdered(scoreDaily, DAILY, item);

    return List.of(reviewByAllTime, reviewMonthly, reviewWeekly, reviewDaily);
  }

  private BigDecimal getScore(long periodLikeCount, long periodCommentCount) {
    BigDecimal likePart = BigDecimal.valueOf(periodLikeCount).multiply(LIKE_WEIGHT);
    BigDecimal commentPart = BigDecimal.valueOf(periodCommentCount).multiply(COMMENT_WEIGHT);

    BigDecimal score = likePart.add(commentPart);

    // 일관된 소수 자리수와 반올림 방식 적용
    return score.setScale(RESULT_SCALE, ROUNDING);
  }
}
