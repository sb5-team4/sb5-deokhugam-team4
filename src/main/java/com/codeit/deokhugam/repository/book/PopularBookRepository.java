package com.codeit.deokhugam.repository.book;

import com.codeit.deokhugam.domain.entity.PopularBook;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PopularBookRepository extends JpaRepository<PopularBook, Long>,
    PopularBookQueryRepository {

}
