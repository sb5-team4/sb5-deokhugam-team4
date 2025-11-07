## 📚 Deokhugam
책 읽는 즐거움을 공유하고, 지식과 감상을 나누는 **책 덕후들의 커뮤니티 서비스**


## 🗓️ Project Overview
- **개발 기간**: 2025.10.20 ~ 2025.11.07 (총 3주)
- **개발 인원**: 권지인 · 임규성 · 서찬규 · 정건진 (총 4명)


## 🧰 Tech Stack

### 🖥️ Front-end
![HTML5](https://img.shields.io/badge/html5-%23E34F26.svg?style=for-the-badge&logo=html5&logoColor=white)
![CSS3](https://img.shields.io/badge/css3-%231572B6.svg?style=for-the-badge&logo=css3&logoColor=white)
![JavaScript](https://img.shields.io/badge/javascript-%23323330.svg?style=for-the-badge&logo=javascript&logoColor=%23F7DF1E)


### ⚙️ Back-end
![Java](https://img.shields.io/badge/java-%23ED8B00.svg?style=for-the-badge&logo=openjdk&logoColor=white)
![Spring Boot](https://img.shields.io/badge/springboot-%236DB33F.svg?style=for-the-badge&logo=springboot&logoColor=white)
![Spring JPA](https://img.shields.io/badge/Spring%20JPA-%236DB33F.svg?style=for-the-badge&logo=spring&logoColor=white)
![Spring Batch](https://img.shields.io/badge/Spring%20Batch-%236DB33F.svg?style=for-the-badge&logo=spring&logoColor=white)
![Querydsl](https://img.shields.io/badge/Querydsl-%234285F4.svg?style=for-the-badge&logo=google&logoColor=white)
![Gradle](https://img.shields.io/badge/Gradle-02303A.svg?style=for-the-badge&logo=Gradle&logoColor=white)


### 🗄️ Database
![Postgres](https://img.shields.io/badge/postgres-%23316192.svg?style=for-the-badge&logo=postgresql&logoColor=white)
![H2 Database](https://img.shields.io/badge/H2%20Database-316192?style=for-the-badge&logo=database&logoColor=white)


### 🌐 Server & Infra
![Nginx](https://img.shields.io/badge/nginx-%23009639.svg?style=for-the-badge&logo=nginx&logoColor=white)
![Apache Tomcat](https://img.shields.io/badge/apache%20tomcat-%23F8DC75.svg?style=for-the-badge&logo=apache-tomcat&logoColor=black)


### ☁️ Cloud
![Docker](https://img.shields.io/badge/docker-%230db7ed.svg?style=for-the-badge&logo=docker&logoColor=white)
![AWS](https://img.shields.io/badge/AWS-%23FF9900.svg?style=for-the-badge&logo=amazon-aws&logoColor=white)
![Amazon ECR](https://img.shields.io/badge/Amazon%20ECR-%23FF9900.svg?style=for-the-badge&logo=amazonaws&logoColor=white)
![Amazon EC2](https://img.shields.io/badge/Amazon%20EC2-%23FF9900.svg?style=for-the-badge&logo=amazonec2&logoColor=white)
![Amazon RDS](https://img.shields.io/badge/Amazon%20RDS-%23527FFF.svg?style=for-the-badge&logo=amazonrds&logoColor=white)
![Amazon S3](https://img.shields.io/badge/Amazon%20S3-FF9900?style=for-the-badge&logo=amazons3&logoColor=white)


### 🔗 Open API
![Naver API](https://img.shields.io/badge/NAVER%20API-%2303C75A.svg?style=for-the-badge&logo=naver&logoColor=white)
![OCR Space API](https://img.shields.io/badge/OCR%20Space%20API-%230072C6.svg?style=for-the-badge&logo=azure-functions&logoColor=white)


### 🧰 Development Tools
![IntelliJ IDEA](https://img.shields.io/badge/IntelliJIDEA-000000.svg?style=for-the-badge&logo=intellij-idea&logoColor=white)
![Swagger](https://img.shields.io/badge/-Swagger-%23Clojure?style=for-the-badge&logo=swagger&logoColor=white)
![Postman](https://img.shields.io/badge/Postman-%23FF6C37.svg?style=for-the-badge&logo=postman&logoColor=white)
![Codecov](https://img.shields.io/badge/Codecov-%23F01F7A.svg?style=for-the-badge&logo=codecov&logoColor=white)
![Git](https://img.shields.io/badge/git-%23F05033.svg?style=for-the-badge&logo=git&logoColor=white)
![GitHub](https://img.shields.io/badge/github-%23121011.svg?style=for-the-badge&logo=github&logoColor=white)
![GitHub Actions](https://img.shields.io/badge/github%20actions-%232671E5.svg?style=for-the-badge&logo=githubactions&logoColor=white)


### 🤝 Collaboration
![Notion](https://img.shields.io/badge/Notion-%23000000.svg?style=for-the-badge&logo=notion&logoColor=white)
![Figma](https://img.shields.io/badge/figma-%23F24E1E.svg?style=for-the-badge&logo=figma&logoColor=white)
![Discord](https://img.shields.io/badge/Discord-%235865F2.svg?style=for-the-badge&logo=discord&logoColor=white)
![ZEP](https://img.shields.io/badge/ZEP-%236C5CE7.svg?style=for-the-badge&logo=zepeto&logoColor=white)

## 주요 기능

### 🧑‍💻 1. 회원 관리 (User Management)

* **핵심 정보:** 이메일, 닉네임, 비밀번호
* **유효성 검사:** 각 정보에 대한 Bean Validation 적용
* **등록 (회원가입):**
    * 이메일은 **중복될 수 없습니다.**
* **로그인:**
    * 이메일, 비밀번호로 로그인합니다.
    * 로그인 성공 시, 이후 모든 요청 헤더에 `Deokhugam-Request-User-ID`로 사용자의 ID가 포함됩니다.
    * 홈, 로그인을 제외한 모든 API는 **로그인한 사용자만 접근**할 수 있습니다. (Interceptor/Filter 처리 필요)
* **수정:**
    * **닉네임**만 수정할 수 있습니다.
* **삭제 (탈퇴):**
    * **논리 삭제(Soft Delete)**를 기본 원칙으로 합니다. (관련 정보 유지)
    * [배치] 논리 삭제 후 **1일이 경과**하면 사용자 정보를 **물리 삭제(Hard Delete)**합니다.
    * 물리 삭제 시, 해당 사용자와 관련된 모든 정보(리뷰, 댓글, 좋아요 등)가 함께 삭제됩니다.


### 📚 2. 도서 관리 (Book Management)

* **핵심 정보:** 제목, 저자, 소개, 출판사, 출간일, ISBN(Optional), 썸네일 URL(Optional), 리뷰 수, 평점
* **유효성 검사:** 각 정보에 대한 Bean Validation 적용
* **등록:**
    * **ISBN**은 중복될 수 없습니다.
    * **Naver Book API**를 활용해 ISBN으로 책 정보를 자동으로 등록합니다.
    * (심화) **OCR Space API**를 활용해 이미지에서 ISBN을 추출하여 입력할 수 있습니다.
    * 썸네일 이미지는 **AWS S3** 저장소에 저장하고 URL을 관리합니다.
* **수정:**
    * **ISBN**은 수정할 수 없습니다.
* **삭제:**
    * **논리 삭제**를 기본 원칙으로 합니다. (관련 리뷰, 댓글 유지)
    * 물리 삭제 시, 해당 도서와 관련된 모든 정보(리뷰, 댓글)도 함께 삭제됩니다. (단, 이 기능은 UI로 제공하지 않으며 테스트 코드로 검증합니다.)
* **목록 조회 (검색 및 페이지네이션):**
    * **키워드 검색:** 제목, 저자 이름, ISBN 중 하나라도 **부분 일치**하는 데이터 검색
    * **정렬:** `title`(제목), `publishedDate`(출판일), `rating`(평점), `reviewCount`(리뷰 수) 중 **1개의 조건**으로 정렬
    * **커서 페이지네이션:** 정확한 페이지네이션을 위해 내부적으로 `after`(이전 페이지 마지막 요소의 생성 시간)를 2차 정렬 조건으로 활용합니다.


### ✍️ 3. 리뷰 관리 (Review Management)

* **핵심 정보:** 도서 정보, 사용자 정보, 평점(1~5), 내용, 좋아요 수, 댓글 수
* **등록:**
    * 사용자는 **도서 1개당 1개의 리뷰**만 등록할 수 있습니다.
* **수정:**
    * **본인이 작성한 리뷰**만 수정할 수 있습니다.
* **삭제:**
    * **논리 삭제**를 기본 원칙으로 합니다.
    * (중요) 인기 도서, 파워 유저 점수 산출 시 **논리 삭제된 데이터도 통계에 포함**합니다.
* **목록 조회 (검색 및 페이지네이션):**
    * **키워드 검색:** 리뷰 작성자 닉네임, 리뷰 내용, 도서 제목 **부분 일치** 검색
    * **필터링 (완전 일치):** `authorId`(작성자 ID), `bookId`(도서 ID)로 조회 (여러 조건 시 AND)
    * **정렬:** `createdAt`(최신순), `rating`(평점순) 중 **1개의 조건**으로 정렬
    * **페이지네이션:** `after`(생성 시간) 값을 2차 정렬 조건으로 활용합니다.
    * **좋아요 여부:** 요청 헤더의 사용자 ID를 기준으로 **`likedByMe` (boolean)** 값을 응답에 포함합니다.


### 💬 4. 댓글 관리 (Comment Management)

* **핵심 정보:** 리뷰 정보, 사용자 정보, 내용
* **등록:**
    * 특정 리뷰에 대해 댓글을 등록합니다.
* **수정:**
    * **본인이 작성한 댓글**만 수정할 수 있습니다.
* **삭제:**
    * **논리 삭제**를 기본 원칙으로 합니다.
    * (중요) 인기 리뷰, 파워 유저 점수 산출 시 **논리 삭제된 데이터도 통계에 포함**합니다.
* **목록 조회 (페이지네이션):**
    * **시간 순**으로 정렬합니다.
    * **페이지네이션:** `after`(생성 시간) 값을 2차 정렬 조건으로 활용합니다.


### 📈 5. 대시보드 관리 (Dashboard - Batch)

* **공통:**
    * 기간별 (일간, 주간, 월간, 역대) 순위를 산출합니다.
    * 모든 연산은 **매일 Spring Batch**로 수행됩니다.
* **인기 도서:**
    * 점수 = (기간 내 리뷰 수 * 0.4) + (기간 내 평점 평균 * 0.6)
* **인기 리뷰:**
    * 점수 = (기간 내 좋아요 수 * 0.3) + (기간 내 댓글 수 * 0.7)
* **파워 유저:**
    * 활동 점수 = (기간 내 작성 리뷰의 인기 점수 * 0.5) + (참여한 좋아요 수 * 0.2) + (참여한 댓글 수 * 0.3)


### 🔔 6. 알림 관리 (Notification Management)

* **핵심 정보:** 리뷰 정보, 사용자 정보, 내용, 확인 여부
* **등록 (자동 생성):**
    1.  내 리뷰에 **좋아요**가 달릴 때
    2.  내 리뷰에 **댓글**이 달릴 때
    3.  내 리뷰가 기간별 **인기 리뷰 10위 내**에 선정될 때
* **수정 (읽음 처리):**
    * 알림을 **개별적**으로 확인할 수 있습니다.
    * **모든 알림을 한 번에 확인**할 수 있습니다.
* **삭제 (자동화 - Batch):**
    * [배치] **확인한 알림** 중 **1주일이 경과**된 알림은 매일 자동으로 삭제됩니다.
* **목록 조회:**
    * **최근 시간 순**으로 정렬합니다.
    * **페이지네이션**을 구현합니다.


## 👥 Team Members & Tasks

| 이름 | 역할 / 주요 개발 내용 |
|------|----------------------|
| **권지인** | - 📚 도서 관련 API 개발<br>- 🔍 NAVER API 활용<br>- 🧠 OCR Space API 활용<br>- 🏆 인기 도서 Batch 개발<br>- ⚙️ GitHub CD 셋팅<br>- 🗓️ 날짜별 로그 S3 적재 개발<br>- ☁️ AWS ECR / EC2 / S3 셋팅 |
| **임규성** | 👑 **4팀 팀장**<br>- 📝 리뷰 관련 API 개발<br>- 🔔 알림 관련 API 개발<br>- 🏆 인기 리뷰 Batch 개발<br>- 🧹 물리 삭제 Batch 개발<br>- ⚙️ GitHub CI 셋팅<br>- 🐳 Docker 셋팅 |
| **서찬규** | - 👥 유저 관련 API 개발<br>- 💪 파워 유저 Batch 개발 |
| **정건진** | - 💬 댓글 관련 API 개발<br>- 🔔 댓글 생성 시 알림 API 개발<br>- 🧹 알림 삭제 Batch 개발<br>- 🚨 공통 예외 처리 개발<br>- ✅ 유효성 검사 처리 개발 |


## Project tree
<pre>
── 📂 build
   └── 📂 classes
      └── 📂 java
          └── 📂 main
             └── 📂 com
                 └── 📂 codeit
                     └── 📂 deokhugam
                         ├── DeokhugamApplication.class
                         ├── 📂 batch
                         │   ├── MainScheduler.class
                         │   ├── 📂 common
                         │   │   ├── BatchCustomException.class
                         │   │   └── BatchErrorCode.class
                         │   ├── 📂 hardDelete
                         │   │   └── 📂 config
                         │   │       ├── HardDeleteBatchConfig$1.class
                         │   │       └── HardDeleteBatchConfig.class
                         │   ├── 📂 log
                         │   │   ├── LogUploadScheduler.class
                         │   │   └── LogUploadService.class
                         │   ├── 📂 notification
                         │   │   ├── NotificationBatchScheduler.class
                         │   │   ├── NotificationReader.class
                         │   │   ├── NotificationWriter.class
                         │   │   └── 📂 config
                         │   │       └── NotificationBatchConfig.class
                         │   ├── 📂 popularBook
                         │   │   ├── PopularBookBatchConfig.class
                         │   │   ├── PopularBookProcessor.class
                         │   │   ├── PopularBookReader.class
                         │   │   ├── PopularBookScheduler.class
                         │   │   ├── PopularBookWriter.class
                         │   │   └── 📂 dto
                         │   │       ├── PopularBookDto$PopularBookDtoBuilder.class
                         │   │       └── PopularBookDto.class
                         │   └── 📂 popularReview
                         │       ├── PopularReviewProcessor.class
                         │       ├── PopularReviewReader.class
                         │       ├── PopularReviewWriter.class
                         │       ├── RankCounter.class
                         │       └── 📂 config
                         │           ├── PopularReviewBatchConfig$1.class
                         │           └── PopularReviewBatchConfig.class
                         ├── 📂 cache
                         │   ├── InMemoryThrottleCache.class
                         │   └── ThrottleCache.class
                         ├── 📂 common
                         │   └── 📂 exception
                         │       ├── AuthorizationException.class
                         │       ├── ResourceNotFoundException.class
                         │       └── 📂 handler
                         │           ├── CustomException.class
                         │           ├── ErrorCode.class
                         │           ├── ErrorResponse.class
                         │           └── GlobalExceptionHandler.class
                         ├── 📂 config
                         │   ├── AppConfig.class
                         │   ├── MDCLoggingInterceptor.class
                         │   ├── PasswordEncoderConfig.class
                         │   ├── QuerydslConfig.class
                         │   ├── RestTemplateConfig.class
                         │   └── S3Config.class
                         ├── 📂 controller
                         │   ├── CommentController.class
                         │   ├── MemberController.class
                         │   ├── 📂 book
                         │   │   ├── BookController.class
                         │   │   └── PopularBookController.class
                         │   ├── 📂 notification
                         │   │   └── NotificationController.class
                         │   ├── 📂 review
                         │   │   └── ReviewController.class
                         │   └── 📂 v2
                         │       └── ReviewControllerV2.class
                         ├── 📂 domain
                         │   ├── 📂 entity
                         │   │   ├── Book$BookBuilder.class
                         │   │   ├── Book$BookBuilderImpl.class
                         │   │   ├── Book.class
                         │   │   ├── Comment$CommentBuilder.class
                         │   │   ├── Comment$CommentBuilderImpl.class
                         │   │   ├── Comment.class
                         │   │   ├── Member$MemberBuilder.class
                         │   │   ├── Member$MemberBuilderImpl.class
                         │   │   ├── Member.class
                         │   │   ├── Notification$NotificationBuilder.class
                         │   │   ├── Notification$NotificationBuilderImpl.class
                         │   │   ├── Notification.class
                         │   │   ├── PopularBook$PopularBookBuilder.class
                         │   │   ├── PopularBook$PopularBookBuilderImpl.class
                         │   │   ├── PopularBook.class
                         │   │   ├── PopularReview$PopularReviewBuilder.class
                         │   │   ├── PopularReview$PopularReviewBuilderImpl.class
                         │   │   ├── PopularReview.class
                         │   │   ├── PowerMember.class
                         │   │   ├── QBook.class
                         │   │   ├── QComment.class
                         │   │   ├── QMember.class
                         │   │   ├── QNotification.class
                         │   │   ├── QPopularBook.class
                         │   │   ├── QPopularReview.class
                         │   │   ├── QPowerMember.class
                         │   │   ├── QReview.class
                         │   │   ├── QReviewLike.class
                         │   │   ├── Review$ReviewBuilder.class
                         │   │   ├── Review$ReviewBuilderImpl.class
                         │   │   ├── Review.class
                         │   │   ├── ReviewLike.class
                         │   │   └── 📂 base
                         │   │       ├── BaseEntity$BaseEntityBuilder.class
                         │   │       ├── BaseEntity.class
                         │   │       ├── BaseUpdatableEntity$BaseUpdatableEntityBuilder.class
                         │   │       ├── BaseUpdatableEntity.class
                         │   │       ├── QBaseEntity.class
                         │   │       └── QBaseUpdatableEntity.class
                         │   └── 📂 enums
                         │       ├── Period.class
                         │       └── ReviewOrderBy.class
                         ├── 📂 dto
                         │   ├── 📂 command
                         │   │   ├── CreateReviewCommand$CreateReviewCommandBuilder.class
                         │   │   ├── CreateReviewCommand.class
                         │   │   ├── GetNotificationCommand$GetNotificationCommandBuilder.class
                         │   │   ├── GetNotificationCommand.class
                         │   │   ├── GetReviewsCommand$GetReviewsCommandBuilder.class
                         │   │   ├── GetReviewsCommand.class
                         │   │   ├── HardDeleteReviewCommand$HardDeleteReviewCommandBuilder.class
                         │   │   ├── HardDeleteReviewCommand.class
                         │   │   ├── PatchReviewCommand$PatchReviewCommandBuilder.class
                         │   │   ├── PatchReviewCommand.class
                         │   │   ├── ReadNotificationCommand$ReadNotificationCommandBuilder.class
                         │   │   ├── ReadNotificationCommand.class
                         │   │   ├── SoftDeleteReviewCommand$SoftDeleteReviewCommandBuilder.class
                         │   │   ├── SoftDeleteReviewCommand.class
                         │   │   ├── 📂 book
                         │   │   │   ├── BookCreateCommand$BookCreateCommandBuilder.class
                         │   │   │   ├── BookCreateCommand.class
                         │   │   │   ├── BookInfoByIsbnCommand$BookInfoByIsbnCommandBuilder.class
                         │   │   │   ├── BookInfoByIsbnCommand.class
                         │   │   │   ├── BookListCommand$BookListCommandBuilder.class
                         │   │   │   ├── BookListCommand.class
                         │   │   │   ├── BookUpdateCommand$BookUpdateCommandBuilder.class
                         │   │   │   ├── BookUpdateCommand.class
                         │   │   │   ├── IsbnOcrCommand$IsbnOcrCommandBuilder.class
                         │   │   │   ├── IsbnOcrCommand.class
                         │   │   │   ├── PopularBookCommand$PopularBookCommandBuilder.class
                         │   │   │   └── PopularBookCommand.class
                         │   │   ├── 📂 comment
                         │   │   │   ├── CommentCreateCommand.class
                         │   │   │   ├── CommentHardDeleteCommand.class
                         │   │   │   ├── CommentSoftDeleteCommand.class
                         │   │   │   ├── CommentUpdateCommand.class
                         │   │   │   └── CursorPageCommentCommand.class
                         │   │   ├── 📂 member
                         │   │   │   ├── MemberCreateCommand.class
                         │   │   │   ├── MemberLoginCommand.class
                         │   │   │   └── PowerMemberFindCommand.class
                         │   ├── 📂 request
                         │   │   ├── NotificationRequest$NotificationRequestBuilder.class
                         │   │   ├── NotificationRequest.class
                         │   │   ├── PatchReviewRequest$PatchReviewRequestBuilder.class
                         │   │   ├── PatchReviewRequest.class
                         │   │   ├── 📂 book
                         │   │   │   ├── BookCreateRequest$BookCreateRequestBuilder.class
                         │   │   │   ├── BookCreateRequest.class
                         │   │   │   ├── BookListRequest$BookListRequestBuilder.class
                         │   │   │   ├── BookListRequest.class
                         │   │   │   ├── BookUpdateRequest.class
                         │   │   │   ├── IsbnOcrRequest$IsbnOcrRequestBuilder.class
                         │   │   │   ├── IsbnOcrRequest.class
                         │   │   │   ├── PopularBookRequest$PopularBookRequestBuilder.class
                         │   │   │   └── PopularBookRequest.class
                         │   │   ├── 📂 comment
                         │   │   │   ├── CommentCreateRequest$CommentCreateRequestBuilder.class
                         │   │   │   ├── CommentCreateRequest.class
                         │   │   │   ├── CommentUpdateRequest$CommentUpdateRequestBuilder.class
                         │   │   │   └── CommentUpdateRequest.class
                         │   │   ├── 📂 member
                         │   │   │   ├── MemberCreateRequest.class
                         │   │   │   ├── MemberLoginRequest.class
                         │   │   │   └── MemberUpdateRequest.class
                         │   │   ├── 📂 review
                         │   │   │   ├── CreateReviewRequest$CreateReviewRequestBuilder.class
                         │   │   │   └── CreateReviewRequest.class
                         │   ├── 📂 response
                         │   │   ├── CursorPageResponse$CursorPageResponseBuilder.class
                         │   │   ├── CursorPageResponse.class
                         │   │   ├── LikeReviewResponse$LikeReviewResponseBuilder.class
                         │   │   ├── LikeReviewResponse.class
                         │   │   ├── NotificationResponse$NotificationResponseBuilder.class
                         │   │   ├── NotificationResponse.class
                         │   │   ├── PopularReviewResponse$PopularReviewResponseBuilder.class
                         │   │   ├── PopularReviewResponse.class
                         │   │   ├── 📂 book
                         │   │   │   ├── BookListResponse$BookItem$BookItemBuilder.class
                         │   │   │   ├── BookListResponse$BookItem.class
                         │   │   │   ├── BookListResponse$BookListResponseBuilder.class
                         │   │   │   ├── BookListResponse.class
                         │   │   │   ├── BookResponse$BookResponseBuilder.class
                         │   │   │   ├── BookResponse.class
                         │   │   │   ├── NaverBookResponse$NaverBookResponseBuilder.class
                         │   │   │   ├── NaverBookResponse.class
                         │   │   │   ├── NaverBookSearchResponse$Item.class
                         │   │   │   ├── NaverBookSearchResponse.class
                         │   │   │   ├── PopularBookListResponse$PopularBookListResponseBuilder.class
                         │   │   │   ├── PopularBookListResponse$PopularBookResponse$PopularBookResponseBuilder.class
                         │   │   │   ├── PopularBookListResponse$PopularBookResponse.class
                         │   │   │   └── PopularBookListResponse.class
                         │   │   ├── 📂 comment
                         │   │   │   ├── CommentResponse.class
                         │   │   │   └── CursorPageCommentResponse.class
                         │   │   ├── 📂 member
                         │   │   │   ├── MemberCreatedResponse.class
                         │   │   │   ├── MemberFindResponse.class
                         │   │   │   ├── MemberLoginResponse.class
                         │   │   │   ├── MemberUpdateResponse.class
                         │   │   │   ├── PowerMemberDto.class
                         │   │   │   └── PowerMemberFindResponse.class
                         │   │   └── 📂 review
                         │   │       ├── ReviewResponse$ReviewResponseBuilder.class
                         │   │       └── ReviewResponse.class
                         │   └── 📂 result
                         │       ├── CreateReviewResult$CreateReviewResultBuilder.class
                         │       ├── CreateReviewResult.class
                         │       ├── GetNotificationOneResult$GetNotificationOneResultBuilder.class
                         │       ├── GetNotificationOneResult.class
                         │       ├── GetNotificationResult$GetNotificationResultBuilder.class
                         │       ├── GetNotificationResult.class
                         │       ├── GetPopularReviewsResult$GetPopularReviewsResultBuilder.class
                         │       ├── GetPopularReviewsResult.class
                         │       ├── GetReviewOneResult$GetReviewOneResultBuilder.class
                         │       ├── GetReviewOneResult.class
                         │       ├── GetReviewsResult$GetReviewsResultBuilder.class
                         │       ├── GetReviewsResult.class
                         │       ├── PaginatedResult$PaginatedResultBuilder.class
                         │       ├── PaginatedResult.class
                         │       ├── PatchReviewResult$PatchReviewResultBuilder.class
                         │       ├── PatchReviewResult.class
                         │       ├── PopularReviewResult$PopularReviewResultBuilder.class
                         │       ├── PopularReviewResult.class
                         │       ├── ReadNotificationResult$ReadNotificationResultBuilder.class
                         │       ├── ReadNotificationResult.class
                         │       ├── 📂 book
                         │       │   ├── BookCreateResult$BookCreateResultBuilder.class
                         │       │   ├── BookCreateResult.class
                         │       │   ├── BookInfoByIsbnResult$BookInfoByIsbnResultBuilder.class
                         │       │   ├── BookInfoByIsbnResult.class
                         │       │   ├── BookListResult$BookListResultBuilder.class
                         │       │   ├── BookListResult$BookResult$BookResultBuilder.class
                         │       │   ├── BookListResult$BookResult.class
                         │       │   ├── BookListResult.class
                         │       │   ├── BookUpdateResult$BookUpdateResultBuilder.class
                         │       │   ├── BookUpdateResult.class
                         │       │   ├── IsbnOcrResult$IsbnOcrResultBuilder.class
                         │       │   ├── IsbnOcrResult.class
                         │       │   ├── PopularBookListResult$PopularBookListResultBuilder.class
                         │       │   ├── PopularBookListResult$PopularBookResult$PopularBookResultBuilder.class
                         │       │   ├── PopularBookListResult$PopularBookResult.class
                         │       │   └── PopularBookListResult.class
                         │       ├── 📂 comment
                         │       │   ├── CommentCreateResult.class
                         │       │   ├── CommentUpdateResult.class
                         │       │   └── CursorPageCommentResult.class
                         │       └── 📂 member
                         │           ├── MemberCreatedResult.class
                         │           ├── MemberFindResult.class
                         │           ├── MemberLoginResult.class
                         │           ├── MemberUpdateResult.class
                         │           └── PowerMemberFindResult.class
                         ├── 📂 mapper
                         │   ├── CommentMapper.class
                         │   ├── CommentMapperImpl.class
                         │   ├── MemberMapper.class
                         │   ├── MemberMapperImpl.class
                         │   ├── NotificationMapper.class
                         │   ├── NotificationMapperImpl.class
                         │   ├── 📂 book
                         │   │   ├── BookMapper.class
                         │   │   ├── BookMapperImpl.class
                         │   │   ├── IsbnOcrMapper.class
                         │   │   ├── PopularBookMapper.class
                         │   │   └── PopularBookMapperImpl.class
                         │   ├── 📂 likeReview
                         │   │   ├── LikeReviewMapper.class
                         │   │   └── LikeReviewMapperImpl.class
                         │   └── 📂 review
                         │       ├── ReviewMapper.class
                         │       └── ReviewMapperImpl.class
                         ├── 📂 repository
                         │   ├── CommentRepository.class
                         │   ├── MemberRepository.class
                         │   ├── NotificationQueryRepository.class
                         │   ├── NotificationRepository.class
                         │   ├── PopularReviewQueryRepository.class
                         │   ├── PopularReviewRepository.class
                         │   ├── ReviewLikeRepository.class
                         │   ├── ReviewQueryRepository.class
                         │   ├── ReviewRepository.class
                         │   ├── 📂 book
                         │   │   ├── BookQueryRepository.class
                         │   │   ├── BookRepository.class
                         │   │   ├── PopularBookQueryRepository.class
                         │   │   ├── PopularBookRepository.class
                         │   │   └── 📂 impl
                         │   │       ├── BookQueryRepositoryImpl.class
                         │   │       └── PopularBookQueryRepositoryImpl.class
                         │   ├── 📂 comment
                         │   │   └── CommentRepositoryCustom.class
                         │   └── 📂 impl
                         │       ├── CommentRepositoryImpl.class
                         │       ├── MemberQueryRepository.class
                         │       ├── NotificationQueryRepositoryImpl.class
                         │       ├── PopularReviewQueryRepositoryImpl.class
                         │       └── ReviewQueryRepositoryImpl.class
                         └── 📂 service
                             ├── CommentService.class
                             ├── GetPopularReviewsCommand$GetPopularReviewsCommandBuilder.class
                             ├── GetPopularReviewsCommand.class
                             ├── GetReviewService.class
                             ├── LikeReviewCommand$LikeReviewCommandBuilder.class
                             ├── LikeReviewCommand.class
                             ├── LikeReviewResult$LikeReviewResultBuilder.class
                             ├── LikeReviewResult.class
                             ├── LikeReviewService.class
                             ├── NotificationService.class
                             ├── ReviewService.class
                             ├── 📂 book
                             │   ├── BookService.class
                             │   ├── IsbnOcrService.class
                             │   ├── NaverApiService.class
                             │   ├── PopularBookService.class
                             │   ├── RealS3Service.class
                             │   └── S3Service.class
                             └── 📂 impl
                                 ├── GetReviewServiceImpl.class
                                 ├── LikeReviewServiceImpl.class
                                 ├── MemberService.class
                                 └── ReviewServiceImpl.class
</pre>
