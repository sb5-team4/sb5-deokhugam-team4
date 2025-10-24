package com.codeit.deokhugam.service.impl;

import com.codeit.deokhugam.common.exception.AuthorizationException;
import com.codeit.deokhugam.common.exception.ResourceNotFoundException;
import com.codeit.deokhugam.domain.entity.Book;
import com.codeit.deokhugam.domain.entity.Member;
import com.codeit.deokhugam.domain.entity.Review;
import com.codeit.deokhugam.dto.command.CreateReviewCommand;
import com.codeit.deokhugam.dto.command.HardDeleteReviewCommand;
import com.codeit.deokhugam.dto.command.PatchReviewCommand;
import com.codeit.deokhugam.dto.command.SoftDeleteReviewCommand;
import com.codeit.deokhugam.dto.result.CreateReviewResult;
import com.codeit.deokhugam.dto.result.PatchReviewResult;
import com.codeit.deokhugam.repository.BookRepository;
import com.codeit.deokhugam.repository.MemberRepository;
import com.codeit.deokhugam.repository.ReviewLikeRepository;
import com.codeit.deokhugam.repository.ReviewRepository;
import com.codeit.deokhugam.service.ReviewService;
import jakarta.transaction.Transactional;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.Optional;
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
  public CreateReviewResult createReview( // todo 409에러 처리
      CreateReviewCommand command) {

    Long bookId = command.getBookId();
    Book book = bookRepository.findByIdAndDeletedIsFalse(bookId).orElseThrow(
        () -> new ResourceNotFoundException("bookId with" + bookId + " not found", bookId));
    // book의 review_count 증가
    book.setReviewCount(book.getReviewCount() + 1);

    Long memberId = command.getUserId();
    Member member = memberRepository.findByIdAndDeletedIsFalse(memberId)
        .orElseThrow(
            () -> new ResourceNotFoundException("memberId with " + memberId + " not found",
                bookId));

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

    Review savedReview = reviewRepository.save(review);
    return CreateReviewResult.builder()
        .id(savedReview.getId())
        .bookId(bookId)
        .bookTitle(book.getTitle())
        .bookThumbnailUrl(book.getThumbnailUrl())
        .userId(memberId)
        .userNickname(member.getNickname())
        .content(content)
        .rating((short) rate)
        .likeCount(savedReview.getLikeCount())
        .commentCount(savedReview.getCommentCount())
        .likedByMe(isLikedByMe)
        .createdAt(Optional.ofNullable(savedReview.getCreatedAt())
            .map(i -> OffsetDateTime.ofInstant(i, ZoneId.of("Asia/Seoul")))
            .orElse(null))
        .updatedAt(Optional.ofNullable(savedReview.getUpdatedAt())
            .map(i -> OffsetDateTime.ofInstant(i, ZoneId.of("Asia/Seoul")))
            .orElse(null))
        .build();
  }

  @Override
  @Transactional
  public boolean softDelete(SoftDeleteReviewCommand command) { // todo softDelete cascade
    long memberId = command.getMemberId();
    long reviewId = command.getReviewId();

    Review targetReview = reviewRepository.findByIdAndDeletedIsFalse(reviewId).orElseThrow(
        () -> new ResourceNotFoundException("reviewId with " + reviewId + " not found", reviewId));

    if (targetReview.getMember().getId() != memberId) {
      throw new AuthorizationException("허용 되지 않은 연산입니다.");
    }
    targetReview.setDeleted(true);

    Book targetBook = targetReview.getBook();
    targetBook.setReviewCount(targetBook.getReviewCount() - 1);

    return true;
  }

  @Override
  @Transactional
  public boolean hardDelete(HardDeleteReviewCommand command) {
    long memberId = command.getMemberId();
    long reviewId = command.getReviewId();

    Review targetReview = reviewRepository.findById(reviewId).orElseThrow(
        () -> new ResourceNotFoundException("reviewId with " + reviewId + " not found", reviewId));

    if (targetReview.getMember().getId() != memberId) {
      throw new AuthorizationException("허용 되지 않은 연산입니다.");
    }

    // 만약 review 가 "softDeleted 상태가 아니면" ReviewCount 감소
    if (!targetReview.isDeleted()) {
      Book targetBook = targetReview.getBook();
      targetBook.setReviewCount(targetBook.getReviewCount() - 1);
    }

    reviewRepository.deleteById(reviewId);

    return true;
  }


  @Override
  public PatchReviewResult patchReview(PatchReviewCommand command) {
    return null;
  }
}
