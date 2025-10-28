-- data.sql (H2 / 테스트용)
-- 1) MEMBER 20명 생성
INSERT INTO member (email, nickname, password, created_at, updated_at, deleted)
VALUES ('user1@example.com', 'user1', 'password', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, FALSE),
       ('user2@example.com', 'user2', 'password', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, FALSE),
       ('user3@example.com', 'user3', 'password', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, FALSE),
       ('user4@example.com', 'user4', 'password', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, FALSE),
       ('user5@example.com', 'user5', 'password', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, FALSE),
       ('user6@example.com', 'user6', 'password', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, FALSE),
       ('user7@example.com', 'user7', 'password', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, FALSE),
       ('user8@example.com', 'user8', 'password', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, FALSE),
       ('user9@example.com', 'user9', 'password', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, FALSE),
       ('user10@example.com', 'user10', 'password', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, FALSE),
       ('user11@example.com', 'user11', 'password', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, FALSE),
       ('user12@example.com', 'user12', 'password', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, FALSE),
       ('user13@example.com', 'user13', 'password', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, FALSE),
       ('user14@example.com', 'user14', 'password', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, FALSE),
       ('user15@example.com', 'user15', 'password', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, FALSE),
       ('user16@example.com', 'user16', 'password', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, FALSE),
       ('user17@example.com', 'user17', 'password', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, FALSE),
       ('user18@example.com', 'user18', 'password', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, FALSE),
       ('user19@example.com', 'user19', 'password', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, FALSE),
       ('user20@example.com', 'user20', 'password', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, FALSE);

-- 2) POWER_MEMBER 20명 생성 (member_id 1..20에 매핑)
-- 컬럼 순서/이름은 스키마에 맞춰 명시적으로 적음
INSERT INTO power_member (member_id, period, created_at, rank, score, review_score_sum, like_count,
                          comment_count)
VALUES (1, 'ALL_TIME', CURRENT_TIMESTAMP, 1, 100.00, 0.00, 0, 0),
       (2, 'ALL_TIME', CURRENT_TIMESTAMP, 2, 99.50, 0.00, 0, 0),
       (3, 'ALL_TIME', CURRENT_TIMESTAMP, 3, 98.00, 0.00, 0, 0),
       (4, 'ALL_TIME', CURRENT_TIMESTAMP, 4, 97.50, 0.00, 0, 0),
       (5, 'ALL_TIME', CURRENT_TIMESTAMP, 5, 96.00, 0.00, 0, 0),
       (6, 'ALL_TIME', CURRENT_TIMESTAMP, 6, 95.00, 0.00, 0, 0),
       (7, 'ALL_TIME', CURRENT_TIMESTAMP, 7, 94.00, 0.00, 0, 0),
       (8, 'ALL_TIME', CURRENT_TIMESTAMP, 8, 93.00, 0.00, 0, 0),
       (9, 'ALL_TIME', CURRENT_TIMESTAMP, 9, 92.00, 0.00, 0, 0),
       (10, 'ALL_TIME', CURRENT_TIMESTAMP, 10, 91.00, 0.00, 0, 0),
       (11, 'ALL_TIME', CURRENT_TIMESTAMP, 11, 90.00, 0.00, 0, 0),
       (12, 'ALL_TIME', CURRENT_TIMESTAMP, 12, 89.00, 0.00, 0, 0),
       (13, 'ALL_TIME', CURRENT_TIMESTAMP, 13, 88.00, 0.00, 0, 0),
       (14, 'ALL_TIME', CURRENT_TIMESTAMP, 14, 87.00, 0.00, 0, 0),
       (15, 'ALL_TIME', CURRENT_TIMESTAMP, 15, 86.00, 0.00, 0, 0),
       (16, 'ALL_TIME', CURRENT_TIMESTAMP, 16, 85.00, 0.00, 0, 0),
       (17, 'ALL_TIME', CURRENT_TIMESTAMP, 17, 84.00, 0.00, 0, 0),
       (18, 'ALL_TIME', CURRENT_TIMESTAMP, 18, 83.00, 0.00, 0, 0),
       (19, 'ALL_TIME', CURRENT_TIMESTAMP, 19, 82.00, 0.00, 0, 0),
       (20, 'ALL_TIME', CURRENT_TIMESTAMP, 20, 81.00, 0.00, 0, 0);
