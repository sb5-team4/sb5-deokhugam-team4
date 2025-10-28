DROP TABLE IF EXISTS review_like CASCADE;
CREATE TABLE review_like
(
    id         BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY NOT NULL,
    review_id  BIGINT                                          NOT NULL,
    member_id  BIGINT                                          NOT NULL,
    created_at timestamp with time zone                        NOT NULL,
    CONSTRAINT uq_review_like UNIQUE (review_id, member_id)
);

DROP TABLE IF EXISTS popular_review CASCADE;
CREATE TABLE popular_review
(
    id         BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY NOT NULL,
    review_id  BIGINT                                          NOT NULL,
    rank       BIGINT                                          NOT NULL,
    score      DECIMAL(10, 2)                                  NOT NULL,
    period     VARCHAR(10)                                     NOT NULL, -- 'DAILY, MONTLY, YEARLY, ALL'
    created_at timestamp with time zone                        NOT NULL,
    CONSTRAINT uq_popular_review UNIQUE (period, rank)                   -- 기간별 공동 순위를 제거
);

DROP TABLE IF EXISTS comment CASCADE;
CREATE TABLE comment
(
    id         BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY NOT NULL,
    member_id  BIGINT                                          NOT NULL,
    review_id  BIGINT                                          NOT NULL,
    content    VARCHAR(300)                                    NOT NULL,
    created_at timestamp with time zone                        NOT NULL,
    updated_at timestamp with time zone                        NULL,
    deleted    BOOLEAN                                         NOT NULL
);

DROP TABLE IF EXISTS power_member CASCADE;
CREATE TABLE power_member
(
    id               BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY NOT NULL,
    member_id        BIGINT                                          NOT NULL,
    period           VARCHAR(10)                                     NOT NULL,
    created_at       timestamp with time zone                        NOT NULL,
    rank             SMALLINT                                        NOT NULL,
    score            DECIMAL(10, 2)                                  NOT NULL,
    review_score_sum DECIMAL(3, 2)                                   NOT NULL,
    like_count       BIGINT                                          NOT NULL DEFAULT 0,
    comment_count    BIGINT                                          NOT NULL DEFAULT 0
);

DROP TABLE IF EXISTS book CASCADE;
CREATE TABLE book
(
    id             BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY NOT NULL,
    title          VARCHAR(255)                                    NOT NULL,
    author         VARCHAR(255)                                    NOT NULL,
    description    TEXT                                            NOT NULL,
    publisher      VARCHAR(255)                                    NOT NULL,
    published_date DATE                                            NOT NULL,
    isbn           VARCHAR(13)                                     NULL,
    thumbnail_url  VARCHAR(500)                                    NULL,
    review_count   INT                                             NOT NULL,
    rating         DECIMAL(3, 2)                                   NOT NULL,
    deleted        BOOLEAN                                         NOT NULL,
    created_at     timestamp with time zone                        NOT NULL,
    updated_at     timestamp with time zone                        NULL
);

DROP TABLE IF EXISTS review CASCADE;
CREATE TABLE review
(
    id            BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY NOT NULL,
    book_id       BIGINT                                          NOT NULL,
    member_id     BIGINT                                          NOT NULL,
    created_at    timestamp with time zone                        NOT NULL,
    updated_at    timestamp with time zone                        NULL,
    deleted       BOOLEAN                                         NOT NULL,
    rating        SMALLINT                                        NOT NULL,
    content       VARCHAR(1000)                                   NOT NULL,
    like_count    BIGINT                                          NOT NULL DEFAULT 0,
    comment_count BIGINT                                          NOT NULL DEFAULT 0,
    CONSTRAINT uq_review UNIQUE (book_id, member_id)

);

DROP TABLE IF EXISTS notification CASCADE;
CREATE TABLE notification
(
    id         BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY NOT NULL,
    member_Id  BIGINT                                          NOT NULL,
    review_Id  BIGINT                                          NOT NULL,
    content    VARCHAR(50)                                     NOT NULL,
    confirmed  BOOLEAN                                         NOT NULL,
    created_at timestamp with time zone                        NOT NULL,
    updated_at timestamp with time zone                        NULL,
    deleted    BOOLEAN                                         NOT NULL
);

DROP TABLE IF EXISTS member CASCADE;
CREATE TABLE member
(
    id         BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY NOT NULL,
    email      VARCHAR(50)                                     NOT NULL UNIQUE,
    nickname   VARCHAR(50)                                     NOT NULL UNIQUE,
    password   VARCHAR(100)                                    NOT NULL, --'bcrypt'
    created_at timestamp with time zone                        NOT NULL,
    updated_at timestamp with time zone                        NULL,
    deleted    BOOLEAN                                         NOT NULL
);

DROP TABLE IF EXISTS popular_book CASCADE;
CREATE TABLE popular_book
(
    id         BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY NOT NULL,
    book_id    BIGINT                                          NOT NULL,
    period     VARCHAR(20)                                     NOT NULL,
    rank       SMALLINT                                        NOT NULL,
    score      DECIMAL(10, 2)                                  NOT NULL,
    created_at timestamp with time zone                        NOT NULL
);

-- ALTER TABLE review_like
--     ADD CONSTRAINT PK_REVIEW_LIKE PRIMARY KEY (
--                                                review_id,
--                                                member_id
--         );


ALTER TABLE review_like
    ADD CONSTRAINT FK_review_TO_review_like_1 FOREIGN KEY (
                                                           review_id
        )
        REFERENCES review (
                           id
            ) ON DELETE CASCADE
;

ALTER TABLE review_like
    ADD CONSTRAINT FK_member_TO_review_like_1 FOREIGN KEY (
                                                           member_id
        )
        REFERENCES member (
                           id
            ) ON DELETE CASCADE
;

ALTER TABLE popular_review
    ADD CONSTRAINT FK_review_TO_popular_review_1 FOREIGN KEY (
                                                              review_id
        )
        REFERENCES review (
                           id
            ) ON DELETE CASCADE
;

ALTER TABLE comment
    ADD CONSTRAINT FK_member_TO_comment_1 FOREIGN KEY (
                                                       member_id
        )
        REFERENCES member (
                           id
            ) ON DELETE CASCADE
;

ALTER TABLE comment
    ADD CONSTRAINT FK_review_TO_comment_1 FOREIGN KEY (
                                                       review_id
        )
        REFERENCES review (
                           id
            )
        ON DELETE CASCADE
;

ALTER TABLE power_member
    ADD CONSTRAINT FK_member_TO_power_member_1 FOREIGN KEY (
                                                            member_id
        )
        REFERENCES member (
                           id
            )
        ON DELETE CASCADE
;

ALTER TABLE review
    ADD CONSTRAINT FK_book_TO_review_1 FOREIGN KEY (
                                                    book_id
        )
        REFERENCES book (
                         id
            )
        ON DELETE CASCADE
;

ALTER TABLE review
    ADD CONSTRAINT FK_member_TO_review_1 FOREIGN KEY (
                                                      member_id
        )
        REFERENCES member (
                           id
            )
        ON DELETE CASCADE
;

ALTER TABLE notification
    ADD CONSTRAINT FK_member_TO_notification_1 FOREIGN KEY (
                                                            member_Id
        )
        REFERENCES member (
                           id
            )
        ON DELETE CASCADE
;

ALTER TABLE notification
    ADD CONSTRAINT FK_review_TO_notification_1 FOREIGN KEY (
                                                            review_Id
        )
        REFERENCES review (
                           id
            )
        ON DELETE CASCADE
;

ALTER TABLE popular_book
    ADD CONSTRAINT FK_book_TO_popular_book_1 FOREIGN KEY (
                                                          book_id
        )
        REFERENCES book (
                         id
            )
        ON DELETE CASCADE
;

