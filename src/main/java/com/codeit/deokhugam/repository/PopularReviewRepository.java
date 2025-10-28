package com.codeit.deokhugam.repository;

import com.codeit.deokhugam.domain.entity.PopularReview;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PopularReviewRepository
    extends JpaRepository<PopularReview, Long>, PopularReviewQueryRepository {

}
