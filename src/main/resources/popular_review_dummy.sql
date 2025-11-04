-----------------------------------------------------
-- 초기화 : 모든 데이터 삭제 + 시퀀스 초기화
-----------------------------------------------------
TRUNCATE TABLE
    popular_review,
    review_like,
    comment,
    review,
    book,
    member
    RESTART IDENTITY CASCADE;

-----------------------------------------------------
-- 1) MEMBER 100명
-----------------------------------------------------
INSERT INTO member (email, nickname, password, created_at, updated_at, deleted)
SELECT 'user' || i || '@test.com',
       'user' || i,
       'pass',
       NOW() - INTERVAL '7 days', -- created_at 1주일 전
       NOW() - INTERVAL '3 days', -- updated_at 3일 전
       (RANDOM() < 0.5)
FROM generate_series(1, 100) AS i;

-----------------------------------------------------
-- 2) BOOK 100권
-----------------------------------------------------
INSERT INTO book (title, author, description, publisher, published_date,
                  review_count, rating, deleted, created_at, updated_at)
SELECT 'Book' || i,
       'Author' || i,
       'DESC',
       'PUB',
       CURRENT_DATE,
       0,
       (4.0 + (i::float / 100)),
       (RANDOM() < 0.5),
       NOW() - INTERVAL '7 days', -- created_at 1주일 전
       NOW() - INTERVAL '3 days'  -- updated_at 3일 전
FROM generate_series(1, 100) AS i;

-----------------------------------------------------
-- 3) REVIEW 100개
-----------------------------------------------------
INSERT INTO review (book_id, member_id, created_at, updated_at, deleted, rating, content)
SELECT ((i - 1) % 100) + 1,
       ((i - 1) % 100) + 1,
       NOW() - INTERVAL '7 days', -- created_at 1주일 전
       NOW() - INTERVAL '3 days', -- updated_at 3일 전
       (RANDOM() < 0.5),
       (i % 5) + 1,
       'content ' || i
FROM generate_series(1, 100) AS i;

-----------------------------------------------------
-- 4) REVIEW_LIKE 100개
-----------------------------------------------------
INSERT INTO review_like (review_id, member_id, created_at)
SELECT ((i - 1) % 30) + 1,
       ((i - 1) % 100) + 1,
       NOW() - INTERVAL '7 days' -- created_at 1주일 전
FROM generate_series(1, 100) AS i;

-----------------------------------------------------
-- 5) COMMENT 100개
-----------------------------------------------------
INSERT INTO comment (member_id, review_id, content, created_at, updated_at, deleted)
SELECT ((i - 1) % 100) + 1,
       ((i - 1) % 100) + 1,
       'comment ' || i,
       NOW() - INTERVAL '7 days', -- created_at 1주일 전
       NOW() - INTERVAL '3 days', -- updated_at 3일 전
       (RANDOM() < 0.5)
FROM generate_series(1, 100) AS i;

-----------------------------------------------------
-- 6) POPULAR_REVIEW 100개
-----------------------------------------------------
INSERT INTO popular_review (review_id, rank, ordered, score, period, created_at)
SELECT i,
       ((i - 1) % 100) + 1,
       false,
       100 - i,
       CASE
           WHEN i <= 5 THEN 'DAILY'
           WHEN i <= 10 THEN 'WEEKLY'
           WHEN i <= 15 THEN 'MONTHLY'
           ELSE 'ALL_TIME'
           END,
       NOW() - INTERVAL '7 days' -- created_at 1주일 전
FROM generate_series(1, 10) AS i;

-----------------------------------------------------
-- 7) NOTIFICATION 20개
-----------------------------------------------------
INSERT INTO notification (member_id, review_id, content, confirmed, created_at, updated_at, deleted)
SELECT ((i - 1) % 1) + 10,
       ((i - 1) % 20) + 10,
       'Notification content ' || i,
       (RANDOM() < 0.5),
       NOW() - INTERVAL '7 days', -- created_at 1주일 전
       NOW() - INTERVAL '3 days', -- updated_at 3일 전
       (RANDOM() < 0.5)
FROM generate_series(1, 100) AS i;