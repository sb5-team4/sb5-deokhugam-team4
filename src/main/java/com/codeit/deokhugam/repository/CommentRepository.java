package com.codeit.deokhugam.repository;

import com.codeit.deokhugam.domain.entity.Comment;
import com.codeit.deokhugam.repository.comment.CommentRepositoryCustom;
import java.time.Instant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

// JpaRepository와 함께 CommentRepositoryCustom 상속
public interface CommentRepository extends JpaRepository<Comment, Long>, CommentRepositoryCustom {

  long countByReviewId(long reviewId);

  long countByReviewIdAndCreatedAtBetween(long reviewId, Instant from, Instant to);

  @Modifying
  @Query("DELETE FROM Comment c WHERE c.deleted = true AND c.updatedAt < :updatedAt")
  void hardDeleteAllBefore(@Param("updatedAt") Instant updatedAt);

}
