package com.codeit.deokhugam.repository;

import com.codeit.deokhugam.domain.entity.Comment;
import java.time.Instant;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<Comment, Long> {

  long countByReviewId(long reviewId);

  long countByReviewIdAndCreatedAtBetween(long reviewId, Instant from, Instant to);
}
