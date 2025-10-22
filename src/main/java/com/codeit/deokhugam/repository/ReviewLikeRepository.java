package com.codeit.deokhugam.repository;

import com.codeit.deokhugam.domain.entity.ReviewLike;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReviewLikeRepository extends JpaRepository<ReviewLike, Long> {


  Optional<ReviewLike> findByMemberIdAndReviewId(Long memberId, Long reviewId);
}
