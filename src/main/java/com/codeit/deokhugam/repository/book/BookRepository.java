package com.codeit.deokhugam.repository.book;


import com.codeit.deokhugam.batch.popularBook.dto.PopularBookDto;
import com.codeit.deokhugam.domain.entity.Book;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface BookRepository extends JpaRepository<Book, Long>, BookQueryRepository {

  Optional<Book> findByIdAndDeletedIsFalse(Long id);

  Optional<Book> findByIsbn(String Isbn);

  boolean existsByIsbn(String Isbn);

  // 배치 작업용 메서드
  @Query("""
      SELECT new com.codeit.deokhugam.batch.popularBook.dto.PopularBookDto(
          b.id,
          b.title,
          b.author,
          b.thumbnailUrl,
          COUNT(r.id),
          CAST(COALESCE(AVG(r.rating), 0.0) AS double)
      )
      FROM Book b
      LEFT JOIN Review r ON r.book.id = b.id
          AND r.createdAt >= :startDate
          AND r.createdAt < :endDate
      WHERE b.deleted = false
      GROUP BY b.id, b.title, b.author, b.thumbnailUrl
      HAVING COUNT(r.id) > 0
      ORDER BY COUNT(r.id) DESC
      """)
  List<PopularBookDto> findPopularBooksForPeriod(
      @Param("startDate") Instant startDate,
      @Param("endDate") Instant endDate
  );
}
