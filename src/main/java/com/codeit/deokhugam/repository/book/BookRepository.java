package com.codeit.deokhugam.repository.book;


import com.codeit.deokhugam.domain.entity.Book;
import java.time.Instant;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface BookRepository extends JpaRepository<Book, Long>, BookQueryRepository {

  Optional<Book> findByIdAndDeletedIsFalse(Long id);

  Optional<Book> findByIsbn(String Isbn);

  boolean existsByIsbn(String Isbn);

  @Modifying
  @Query("DELETE FROM Book b WHERE b.deleted = true AND b.updatedAt < :updatedAt")
  void hardDeleteAllBefore(@Param("updatedAt") Instant updatedAt);
}
