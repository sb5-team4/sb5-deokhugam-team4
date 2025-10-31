package com.codeit.deokhugam.repository;

import com.codeit.deokhugam.domain.entity.ReviewLike;
import java.time.Instant;
import java.util.Optional;
import java.util.Set;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ReviewLikeRepository extends JpaRepository<ReviewLike, Long> {

  Optional<ReviewLike> findByMemberIdAndReviewId(Long memberId, Long reviewId);

  boolean existsReviewLikeByMemberIdAndReviewId(Long memberId, Long reviewId);


  @Query("""
          select r
          from ReviewLike r
          join fetch r.review
          where r.member.id = :memberId
      """)
  Set<ReviewLike> findAllWithReviewByMemberId(@Param("memberId") Long memberId);

  long countByReviewId(Long reviewId);

  long countByReviewIdAndCreatedAtBetween(Long reviewId, Instant from, Instant to);

}
