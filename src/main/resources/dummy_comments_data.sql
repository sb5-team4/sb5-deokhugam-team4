-- [주의!] 이 스크립트는 모든 기존 데이터를 삭제합니다.
-- 모든 테이블을 비우고 ID 시퀀스를 1부터 다시 시작합니다.
TRUNCATE member, book, review, comment RESTART IDENTITY CASCADE;

-- 1. '더미 유저' 생성 (member_id = 1, 2 생성)
INSERT INTO member (email, nickname, password, deleted, created_at, updated_at)
SELECT
    'user' || i,
    'user' || i,
    'password',
    false,
    NOW(),
    NOW()
FROM generate_series(1, 10) AS i; -- (테스트를 위해 10명만 생성)

-- 2. '더미 책' 생성 (book_id = 1 생성)
-- (보내주신 9개 컬럼에 맞게 수정)
INSERT INTO book (title, author, description, publisher, published_date,
                  review_count, rating, deleted, created_at)
SELECT
    'Book' || i,
    'Author' || i,
    '설명 ' || i,
    '출판사' || i,
    '2025-01-01',
    0,
    4.0,
    false,
    NOW()
FROM generate_series(1, 10) AS i; -- (테스트를 위해 10권만 생성)

-- 3. '더미 리뷰' 생성 (review_id = 1 생성)
-- ★★★★★ 이 부분의 INSERT 순서와 값 개수를 수정했습니다 ★★★★★
-- (1번 유저가 1번 책에 리뷰 작성)
INSERT INTO review (book_id, member_id, created_at, deleted, rating, content)
VALUES (
           1,        -- book_id
           1,        -- member_id
           NOW(),    -- created_at
           false,    -- deleted
           4,        -- rating
           '1번 유저가 1번 책에 쓴 리뷰' -- content
       );

-- 4. '더미 댓글' 생성 (comment_id = 1 생성)
-- (1번 유저가 1번 리뷰에 댓글 작성)
INSERT INTO comment (review_id, member_id, content, deleted, created_at, updated_at)
VALUES (1, 1, '1번 유저가 1번 리뷰에 쓴 댓글', false, NOW(), NOW());

-- 5. (선택적) 다른 유저의 댓글 생성 (403 테스트용)
-- (2번 유저가 1번 리뷰에 댓글 작성 -> comment_id = 2)
INSERT INTO comment (review_id, member_id, content, deleted, created_at, updated_at)
VALUES (1, 2, '2번 유저가 1번 리뷰에 쓴 댓글', false, NOW(), NOW());