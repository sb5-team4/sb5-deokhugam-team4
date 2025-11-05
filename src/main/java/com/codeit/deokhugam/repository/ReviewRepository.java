package com.codeit.deokhugam.repository;


import com.codeit.deokhugam.batch.powerMember.dto.PowerMemberScoreDto;
import com.codeit.deokhugam.domain.entity.Review;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ReviewRepository
    extends JpaRepository<Review, Long>, ReviewQueryRepository {

  Optional<Review> findByIdAndDeletedIsFalse(Long aLong);

  List<Review> findAllByDeletedIsFalse();

  @Modifying
  @Query("DELETE FROM Review r WHERE r.deleted = true AND r.updatedAt < :updatedAt")
  void hardDeleteAllBefore(@Param("updatedAt") Instant updatedAt);

  @Modifying
  @Query("UPDATE Review r SET r.likeCount = r.likeCount + 1 WHERE r.id = :reviewId")
  int incrementLikeCount(@Param("reviewId") Long reviewId);

  @Modifying
  @Query("UPDATE Review r SET r.likeCount = r.likeCount -1 WHERE r.id = :reviewId")
  int decrementLikeCount(@Param("reviewId") Long reviewId);

  long countByMemberId(Long id);

  @Query("""
          SELECT new com.codeit.deokhugam.batch.dto.PowerMemberScoreDto(
              r.member.id,
              SUM(r.rating),
              SUM(r.likeCount),
              SUM(r.commentCount)
          )
          FROM Review r
          WHERE r.createdAt BETWEEN :start AND :end
          GROUP BY r.member.id
      """)
  List<PowerMemberScoreDto> findPowerMemberScoreDto(@Param("start") Instant start,
      @Param("end") Instant end);

}
