package com.codeit.deokhugam.service.impl;

import com.codeit.deokhugam.common.exception.handler.CustomException;
import com.codeit.deokhugam.common.exception.handler.ErrorCode;
import com.codeit.deokhugam.domain.entity.Book;
import com.codeit.deokhugam.domain.entity.Member;
import com.codeit.deokhugam.domain.entity.Review;
import com.codeit.deokhugam.dto.result.GetReviewOneResult;
import com.codeit.deokhugam.repository.BookRepository;
import com.codeit.deokhugam.repository.MemberRepository;
import com.codeit.deokhugam.repository.ReviewLikeRepository;
import com.codeit.deokhugam.repository.ReviewRepository;
import com.codeit.deokhugam.service.GetReviewService;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class GetReviewServiceImpl implements GetReviewService {

  private final ReviewRepository reviewRepository;
  private final BookRepository bookRepository;
  private final MemberRepository memberRepository;
  private final ReviewLikeRepository reviewLikeRepository;

  @Override
  @Transactional(readOnly = true)
  public GetReviewOneResult getReviewOne(Long id, Long memberId) {

    Review review = reviewRepository.findByIdAndDeletedIsFalse(id)
        .orElseThrow(() -> new CustomException(ErrorCode.REVIEW_NOT_FOUND));

    Book book = bookRepository.findByIdAndDeletedIsFalse(review.getBook().getId())
        .orElseThrow(() -> new CustomException(ErrorCode.BOOK_NOT_FOUND));

    Member member = memberRepository.findByIdAndDeletedIsFalse(review.getMember().getId())
        .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

    boolean likedByMe = reviewLikeRepository.existsReviewLikeByMemberIdAndReviewId(memberId, id);

    return GetReviewOneResult.builder()
        .id(id)
        .bookId(book.getId())
        .bookTitle(book.getTitle())
        .bookThumbnailUrl(book.getThumbnailUrl())
        .userId(member.getId())
        .userNickname(member.getNickname())
        .content(review.getContent())
        .rating(review.getRating())
        .likeCount(review.getLikeCount())
        .commentCount(review.getCommentCount())
        .likedByMe(likedByMe)
        .createdAt(Optional.ofNullable(review.getCreatedAt())
            .map(i -> OffsetDateTime.ofInstant(i, ZoneId.of("Asia/Seoul")))
            .orElse(null))
        .updatedAt(Optional.ofNullable(review.getUpdatedAt())
            .map(i -> OffsetDateTime.ofInstant(i, ZoneId.of("Asia/Seoul")))
            .orElse(null))
        .build();
  }
}
