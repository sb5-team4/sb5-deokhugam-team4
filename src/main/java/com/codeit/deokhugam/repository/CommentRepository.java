package com.codeit.deokhugam.repository;

import com.codeit.deokhugam.domain.entity.Comment;
import com.codeit.deokhugam.repository.comment.CommentRepositoryCustom;
import java.time.Instant;
import org.springframework.data.jpa.repository.JpaRepository;

// JpaRepository와 함께 CommentRepositoryCustom 상속
public interface CommentRepository extends JpaRepository<Comment, Long>, CommentRepositoryCustom {


  long countByReviewId(long reviewId);

  long countByReviewIdAndCreatedAtBetween(long reviewId, Instant from, Instant to);
}

