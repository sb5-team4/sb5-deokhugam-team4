package com.codeit.deokhugam.repository;


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

}
