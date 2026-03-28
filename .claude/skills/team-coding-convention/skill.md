---
name: team-coding-convention
description: 코드를 작성하거나 수정할 때 팀 코딩 컨벤션을 적용한다
---

# 팀 코딩 컨벤션

## 네이밍 규칙

- **클래스명**: PascalCase (예: `UserService`, `OrderController`)
- **메서드명**: camelCase (예: `getUserById`, `createOrder`)
- **상수**: UPPER_SNAKE_CASE (예: `MAX_RETRY_COUNT`, `DEFAULT_PAGE_SIZE`)
- **패키지명**: 소문자, 언더스코어 없음 (예: `com.newbranch.backend.domain.user`)

## 메서드 작성 규칙

- 메서드는 한 가지 일만 한다 (Single Responsibility)
- 메서드 길이는 20줄을 넘지 않는다
- 파라미터는 3개 이하로 유지한다
- 메서드명은 동작을 명확히 표현한다 (예: `isValid()`, `calculate()`, `findBy...()`)

## 주석 규칙

- 공개 API에는 반드시 Javadoc 작성
- 복잡한 비즈니스 로직에는 이유를 설명하는 주석 추가
- TODO 주석에는 담당자와 날짜 포함
  ```java
  // TODO(홍길동, 2024-03-28): 캐싱 로직 추가 필요
  ```
- 불필요한 주석은 작성하지 않는다 (코드 자체가 설명이 되도록)

## 테스트 규칙

- 테스트 메서드명은 한글로 작성 가능
  ```java
  @Test
  void 사용자_생성_시_이메일_중복이면_예외_발생() {
      // given-when-then
  }
  ```
- **Given-When-Then** 패턴 사용
- 테스트당 하나의 검증만 수행
- 테스트는 독립적이어야 하며, 실행 순서에 영향받지 않아야 함

## 깃(Git) 브랜치 전략

### 브랜치 종류 (Type)

- **main**: 배포 가능한 상태의 기준 브랜치 (항상 안정적인 상태 유지)
- **develop**: 다음 배포를 위해 기능들이 통합되는 브랜치 (개발 기준점)
- **feat**: 새로운 기능 개발
- **fix**: 버그 수정
- **docs**: 문서 생성 및 수정
- **refactor**: 코드 리팩토링 (기능 변경 없음)
- **test**: 테스트 코드 추가 및 수정
- **chore**: 빌드 설정 수정, 패키지 매니저 설정 등 (프로덕션 코드 변경 없음)

### 브랜치 명명 규칙

- **구조**: `타입/기능요약` (영어 소문자 사용, 띄어쓰기는 하이픈 `-`)
- **예시**:
    - `feat/oauth-login` (OAuth 로그인 기능 개발)
    - `feat/user-profile` (사용자 프로필 기능)
    - `fix/n-plus-one-query` (N+1 쿼리 문제 수정)
    - `fix/null-pointer-exception` (NPE 버그 수정)
    - `docs/update-readme` (README 문서 업데이트)
    - `refactor/member-service` (회원 서비스 리팩토링)
    - `test/user-service` (사용자 서비스 테스트 추가)
    - `chore/update-dependencies` (의존성 버전 업데이트)

### 작업 흐름 (Workflow)

1. 기능 개발 시 `develop` 브랜치에서 최신 내용을 pull 받는다
   ```bash
   git checkout develop
   git pull origin develop
   ```

2. `feat/...` 브랜치를 생성하여 작업한다
   ```bash
   git checkout -b feat/user-authentication
   ```

3. 작업을 진행하며 주기적으로 커밋한다
   ```bash
   git add .
   git commit -m "[Feat] JWT 기반 인증 구현"
   ```

4. 작업이 완료되면 원격 저장소에 push한다
   ```bash
   git push origin feat/user-authentication
   ```

5. `develop` 브랜치로 **Pull Request(PR)**를 보낸다

6. 코드 리뷰(또는 자가 점검/AI 리뷰) 후 `develop`에 Merge 한다

7. Merge 된 기능 브랜치는 삭제한다
   ```bash
   git branch -d feat/user-authentication
   git push origin --delete feat/user-authentication
   ```

## 커밋 메시지 규칙 (Conventional Commits)

### 구조
```
[타입] 제목

(선택) 본문

(선택) 관련 이슈: #이슈번호
```

### 타입 종류

- **[Feat]**: 새로운 기능 추가
- **[Fix]**: 버그 수정
- **[Docs]**: 문서 수정
- **[Refactor]**: 코드 리팩토링 (기능 변경 없음)
- **[Test]**: 테스트 코드 추가/수정
- **[Chore]**: 빌드 설정, 패키지 관리 등
- **[Style]**: 코드 포맷팅, 세미콜론 누락 등 (로직 변경 없음)
- **[Perf]**: 성능 개선

### 예시

```
[Feat] 구글 OAuth 로그인 API 구현

- Google OAuth2 클라이언트 설정
- 로그인/콜백 엔드포인트 추가
- JWT 토큰 발급 로직 구현

관련 이슈: #12
```

```
[Fix] 게시글 조회 시 N+1 문제 해결

- @EntityGraph를 사용하여 연관 엔티티 fetch join
- 쿼리 성능 약 70% 개선

관련 이슈: #45
```

```
[Docs] README 프로젝트 실행 방법 추가
```

```
[Refactor] MemberService 불필요한 중복 코드 제거
```

## 백엔드 폴더 구조

프로젝트는 **도메인 중심 구조(Domain-Driven Design)**를 사용합니다.

### 현재 프로젝트 구조 (DDD)

```
src/main/java/com/newbranch/backend/
├── domain/              # 도메인별 패키지
│   ├── user/           # User 도메인
│   │   ├── controller/
│   │   ├── service/
│   │   ├── repository/
│   │   ├── entity/
│   │   └── dto/
│   ├── order/          # Order 도메인 (예시)
│   │   ├── controller/
│   │   ├── service/
│   │   └── ...
│   └── product/        # Product 도메인 (예시)
│       └── ...
├── global/             # 전역 설정 및 공통 코드
│   ├── config/         # 설정 클래스
│   ├── error/          # 예외 처리
│   ├── common/         # 공통 응답, 유틸리티
│   └── security/       # 보안 설정
└── infrastructure/     # 외부 시스템 연동
    ├── s3/            # AWS S3
    ├── redis/         # Redis
    └── external/      # 외부 API
```

### 도메인 구조 선택 이유

#### ✅ 도메인 중심 구조 (현재 프로젝트)
- **장점**:
    - 비즈니스 도메인별 코드 응집도 높음
    - 확장성이 뛰어남 (새 도메인 추가 용이)
    - 팀 협업 시 도메인별 작업 분리 명확
    - MSA(Microservices Architecture) 전환 용이
- **단점**:
    - 초기 구조 설정이 다소 복잡
    - 소규모 프로젝트에서는 과도할 수 있음
- **추천**: 중규모 이상 프로젝트, 팀 프로젝트

#### ❌ 계층형 구조 (사용 안 함)
```
src/main/java/com/newbranch/backend/
├── controller/   # 모든 Controller
├── service/      # 모든 Service
├── repository/   # 모든 Repository
├── entity/       # 모든 Entity
└── dto/          # 모든 DTO
```
- **장점**:
    - 구조가 단순하고 직관적
    - 소규모 프로젝트에 적합
- **단점**:
    - 프로젝트 커지면 패키지 내 파일 수 급증
    - 도메인 간 경계가 불명확
    - 코드 응집도 낮음
- **추천**: 소규모 프로토타입, 학습용 프로젝트

## 코드 작성 시 체크리스트

코드를 작성하거나 수정할 때 다음을 확인하세요:

- [ ] 네이밍 규칙을 준수했는가?
- [ ] 메서드는 20줄 이하인가?
- [ ] 파라미터는 3개 이하인가?
- [ ] 공개 API에 Javadoc이 있는가?
- [ ] 테스트 코드를 작성했는가?
- [ ] 커밋 메시지가 규칙을 따르는가?
- [ ] 브랜치명이 규칙을 따르는가?
- [ ] 불필요한 주석은 제거했는가?
- [ ] 코드 중복은 없는가?

## 예시 코드

### 좋은 예시 ✅

```java
/**
 * 사용자 정보를 조회합니다.
 *
 * @param userId 사용자 ID
 * @return 사용자 정보
 * @throws BusinessException 사용자를 찾을 수 없는 경우
 */
public UserResponse getUserById(Long userId) {
    User user = userRepository.findById(userId)
            .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
    return UserResponse.from(user);
}
```

### 나쁜 예시 ❌

```java
// 사용자를 가져옴
public UserResponse get(Long id) {  // 메서드명이 불명확
    User u = userRepository.findById(id).orElseThrow();  // 구체적인 예외 없음
    return new UserResponse(u.getId(), u.getEmail(), u.getName(), u.getPhone(), u.getCreatedAt(), u.getUpdatedAt());  // 파라미터 6개
}
```

---

이 컨벤션은 코드의 일관성과 가독성을 높이고, 팀 협업을 원활하게 하기 위한 기준입니다.
새로운 코드를 작성하거나 기존 코드를 수정할 때 반드시 이 컨벤션을 따라주세요.
