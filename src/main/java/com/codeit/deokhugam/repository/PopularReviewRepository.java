package com.codeit.deokhugam.repository;

import com.codeit.deokhugam.domain.entity.PopularReview;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PopularReviewRepository
    extends JpaRepository<PopularReview, Long>, PopularReviewQueryRepository {

  @Modifying
  @Query(value = """
      WITH ranked AS (
          SELECT
              id,
              ROW_NUMBER() OVER (PARTITION BY period ORDER BY score DESC) AS new_rank
          FROM popular_review
          WHERE period = :period
      )
      UPDATE popular_review p
      SET rank = r.new_rank, ordered = true
      FROM ranked r
      WHERE p.id = r.id
      """, nativeQuery = true)
  void updateRankByPeriod(@Param("period") String period);

}
