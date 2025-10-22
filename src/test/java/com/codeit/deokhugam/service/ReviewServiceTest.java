package com.codeit.deokhugam.service;

import com.codeit.deokhugam.repository.BookRepository;
import com.codeit.deokhugam.repository.MemberRepository;
import com.codeit.deokhugam.repository.ReviewLikeRepository;
import com.codeit.deokhugam.repository.ReviewRepository;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@ExtendWith(MockitoExtension.class)
@EnableJpaAuditing
public class ReviewServiceTest {

  @InjectMocks
  private ReviewService reviewService;
  @Mock
  ReviewRepository reviewRepository;
  @Mock
  BookRepository bookRepository;
  @Mock
  MemberRepository memberRepository;
  @Mock
  ReviewLikeRepository reviewLikeRepository;


}
