package com.codeit.deokhugam.repository;


import com.codeit.deokhugam.domain.entity.Book;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookRepository extends JpaRepository<Book, Long> {

}
