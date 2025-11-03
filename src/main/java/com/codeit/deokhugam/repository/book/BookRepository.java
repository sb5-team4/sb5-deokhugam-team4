package com.codeit.deokhugam.repository.book;


import com.codeit.deokhugam.domain.entity.Book;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookRepository extends JpaRepository<Book, Long>, BookQueryRepository {

  Optional<Book> findByIdAndDeletedIsFalse(Long id);

  Optional<Book> findByIsbn(String Isbn);

  boolean existsByIsbn(String Isbn);
}
