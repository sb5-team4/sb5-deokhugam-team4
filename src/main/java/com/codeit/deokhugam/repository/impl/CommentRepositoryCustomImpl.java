package com.codeit.deokhugam.repository.impl;

import com.codeit.deokhugam.mapper.CommentMapper;
import com.codeit.deokhugam.repository.CommentRepository;
import com.codeit.deokhugam.repository.comment.CommentRepositoryCustom;
import com.codeit.deokhugam.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;

public abstract class CommentRepositoryCustomImpl implements CommentRepositoryCustom {

  @Autowired
  private CommentMapper commentMapper;
  @Autowired
  private CommentService commentService;

  @Autowired
  private CommentRepository commentRepository;

}
