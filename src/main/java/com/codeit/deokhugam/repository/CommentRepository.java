package com.codeit.deokhugam.repository;

import com.codeit.deokhugam.domain.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<Comment, Long> {

}
