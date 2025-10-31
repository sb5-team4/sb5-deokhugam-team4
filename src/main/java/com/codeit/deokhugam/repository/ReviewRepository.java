package com.codeit.deokhugam.repository;


import com.codeit.deokhugam.domain.entity.Review;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReviewRepository
    extends JpaRepository<Review, Long>, ReviewQueryRepository {

  Optional<Review> findByIdAndDeletedIsFalse(Long aLong);

  List<Review> findAllByDeletedIsFalse();
}
