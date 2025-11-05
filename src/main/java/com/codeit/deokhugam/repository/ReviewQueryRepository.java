package com.codeit.deokhugam.repository;

import com.codeit.deokhugam.batch.powerMember.dto.PowerMemberScoreDto;
import com.codeit.deokhugam.domain.entity.Review;
import com.codeit.deokhugam.domain.enums.ReviewOrderBy;
import com.codeit.deokhugam.dto.result.PaginatedResult;
import java.time.Instant;
import java.util.List;
import org.springframework.data.domain.Sort.Direction;

public interface ReviewQueryRepository {

  PaginatedResult<Review, String> searchWithCursor(
      Long authorId, // nullable
      Long bookId, // nullable
      String keyword, // nullable
      Direction direction,
      ReviewOrderBy orderBy,
      String cursor, // nullable
      Instant after, // nullable
      Integer limit,
      Long requestUserId,
      Long loginUserId
  );

  /**
   * 파워멤버 점수 계산을 위한 데이터 조회 해당 기간에 작성한 리뷰의 인기 점수 합계, 좋아요 수, 댓글 수를 계산
   *
   * @param start 기간 시작
   * @param end   기간 종료
   * @return 멤버별 점수 DTO 리스트
   */
  List<PowerMemberScoreDto> findPowerMemberScoreDto(Instant start, Instant end);
}