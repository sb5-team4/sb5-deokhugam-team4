-- [주의!] 이미 데이터가 있다면, 아래 주석을 풀고 실행하여 모든 테이블을 비웁니다.
-- (참고: RESTART IDENTITY는 ID를 1부터 다시 시작, CASCADE는 외래 키 관계를 무시하고 삭제)
/*
TRUNCATE member, book, review, comment RESTART IDENTITY CASCADE;
*/

-- 4. 100개의 '더미 댓글' 생성 (PostgreSQL의 generate_series 함수 사용)
-- (1번 유저가 1번 리뷰에 작성)
INSERT INTO comment (review_id, member_id, content, deleted, created_at, updated_at)
SELECT
    1, -- review_id (1번 리뷰)
    1, -- member_id (1번 유저)
    '이것은 ' || i || '번째 테스트 댓글입니다.', -- content (내용)
    false, -- deleted (삭제 안 됨)
    NOW() - (i * interval '1 minute'), -- createdAt (1분 간격으로 과거 시간 설정 - 정렬 테스트용)
    NOW() - (i * interval '1 minute')  -- updatedAt
FROM generate_series(1, 100) AS i; -- 1부터 100까지 반복

-- 5. '더미 리뷰'의 댓글 개수(comment_count) 업데이트
UPDATE review
SET comment_count = 100
WHERE id = 1;
