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
INSERT INTO member (email, nickname, password, created_at, deleted)
SELECT 'user' || i || '@test.com',
       'user' || i,
       'pass',
       NOW(),
       FALSE
FROM generate_series(1, 100) AS i;

-----------------------------------------------------
-- 2) BOOK 100권
-----------------------------------------------------
INSERT INTO book (title, author, description, publisher, published_date,
                  review_count, rating, deleted, created_at)
SELECT 'Book' || i,
       'Author' || i,
       'DESC',
       'PUB',
       CURRENT_DATE,
       0,
       (4.0 + (i::float / 100)),
       FALSE,
       NOW()
FROM generate_series(1, 100) AS i;

-----------------------------------------------------
-- 3) REVIEW 100개
-----------------------------------------------------
INSERT INTO review (book_id, member_id, created_at, deleted, rating, content)
SELECT ((i - 1) % 100) + 1, -- 1~10 반복
       ((i - 1) % 100) + 1,
       NOW() - (i || ' hours')::INTERVAL,
       FALSE,
       (i % 5) + 1,
       'content ' || i
FROM generate_series(1, 100) AS i;

-----------------------------------------------------
-- 4) REVIEW_LIKE 100개
-----------------------------------------------------
INSERT INTO review_like (review_id, member_id, created_at)
SELECT ((i - 1) % 100) + 1, -- review 1~20 반복
       ((i - 1) % 100) + 1, -- member 1~10 반복
       NOW() - (i || ' minutes')::INTERVAL
FROM generate_series(1, 100) AS i;

-----------------------------------------------------
-- 5) COMMENT 100개
-----------------------------------------------------
INSERT INTO comment (member_id, review_id, content, created_at, deleted)
SELECT ((i - 1) % 100) + 1,
       ((i - 1) % 100) + 1,
       'comment ' || i,
       NOW() - (i || ' minutes')::INTERVAL,
       FALSE
FROM generate_series(1, 100) AS i;

-----------------------------------------------------
-- 6) POPULAR_REVIEW 100개
-----------------------------------------------------
INSERT INTO popular_review (review_id, rank, score, period, created_at)
SELECT i,
       ((i - 1) % 100) + 1, -- 1~5 반복 (각 기간별)
       100 - i,
       CASE
           WHEN i <= 5 THEN 'DAILY'
           WHEN i <= 10 THEN 'WEEKLY'
           WHEN i <= 15 THEN 'MONTHLY'
           ELSE 'ALL_TIME'
           END,
       NOW() - ((i / 5) || ' days')::INTERVAL

FROM generate_series(1, 10) AS i;

-----------------------------------------------------
-- 7) NOTIFICATION 20개
-----------------------------------------------------
INSERT INTO notification (member_id, review_id, content, confirmed, created_at, deleted)
SELECT ((i - 1) % 1) + 101,                       -- member 1~10 반복
       ((i - 1) % 20) + 1,                        -- review 1~20 반복
       'Notification content ' || i,
       FALSE,
       NOW() - ((i * 2) || ' minutes')::INTERVAL, -- 2분씩 차이 나게 설정
       FALSE
FROM generate_series(1, 100) AS i;
