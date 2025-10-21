package com.codeit.deokhugam.service.impl;

import com.codeit.deokhugam.domain.entity.Book;
import com.codeit.deokhugam.domain.entity.Member;
import com.codeit.deokhugam.domain.entity.Review;
import com.codeit.deokhugam.dto.command.CreateReviewCommand;
import com.codeit.deokhugam.dto.result.CreateReviewResult;
import com.codeit.deokhugam.repository.BookRepository;
import com.codeit.deokhugam.repository.MemberRepository;
import com.codeit.deokhugam.repository.ReviewLikeRepository;
import com.codeit.deokhugam.repository.ReviewRepository;
import com.codeit.deokhugam.service.ReviewService;
import jakarta.transaction.Transactional;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.NoSuchElementException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {

  private final ReviewRepository reviewRepository;
  private final BookRepository bookRepository;
  private final MemberRepository memberRepository;
  private final ReviewLikeRepository reviewLikeRepository;

  @Override
  @Transactional
  public CreateReviewResult createReview(CreateReviewCommand command) {

    Long bookId = command.getBookId();
    Book book = bookRepository.findById(bookId).orElseThrow(NoSuchElementException::new);
    Long memberId = command.getUserId();
    Member member = memberRepository.findById(memberId).orElseThrow(NoSuchElementException::new);

    int rate = command.getRating();
    String content = command.getContent();

    Review review = Review.builder()
        .deleted(false)
        .rating((short) rate)
        .content(content)
        .book(book)
        .member(member)
        .likeCount(0L)
        .commentCount(0L)
        .build();

    boolean isLikedByMe = reviewLikeRepository
        .findByMemberIdAndReviewId(memberId, review.getId())
        .isPresent();

    reviewRepository.save(review);
    return CreateReviewResult.builder()
        .id(review.getId())
        .bookId(bookId)
        .bookTitle(book.getTitle())
        .bookThumbnailUrl(book.getThumbnailUrl())
        .userId(memberId)
        .userNickname(member.getNickname())
        .content(content)
        .rating((short) rate)
        .likeCount(review.getLikeCount())
        .commentCount(review.getCommentCount())
        .likedByMe(isLikedByMe)
        .createdAt(OffsetDateTime.ofInstant(review.getCreatedAt(), ZoneId.of("Asia/Seoul")))
        .createdAt(OffsetDateTime.ofInstant(review.getUpdatedAt(), ZoneId.of("Asia/Seoul")))
        .build();
  }

}
