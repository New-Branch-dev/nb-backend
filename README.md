# NewBranch Backend

Spring Boot 3.x 기반의 백엔드 API 서버입니다. Next.js 프론트엔드와 협업을 위한 RESTful API를 제공합니다.

> 📘 **프론트엔드 개발자용 가이드**: [API_GUIDE.md](./API_GUIDE.md) 참고

## 기술 스택

- **Runtime:** Java 17 (LTS)
- **Framework:** Spring Boot 3.2.4
- **Build Tool:** Gradle 8.5
- **Database:** PostgreSQL 15
- **Documentation:** Springdoc OpenAPI 3.0
- **Containerization:** Docker & Docker Compose

## 프로젝트 구조

```
src/main/java/com/newbranch/backend/
├── domain/               # 도메인별 패키지 (ERD 설계 후 추가)
│   └── {domain}/
│       ├── controller/   # REST API Controllers
│       ├── service/      # Business Logic
│       ├── repository/   # Data Access Layer
│       ├── entity/       # JPA Entities
│       └── dto/          # Data Transfer Objects
├── global/
│   ├── config/          # Global Configurations (CORS, Swagger)
│   ├── error/           # Exception Handling
│   └── common/          # Common Response Structures
└── infrastructure/      # External API Integration
```

## 프로젝트 현황

현재 **프로젝트 초기 설정 단계**입니다. ERD 설계 및 도메인 개발을 시작하기 전, 협업을 위한 기본 인프라와 개발 환경을 구축한 상태입니다.

### 구성 완료된 항목

- ✅ Spring Boot 기반 프로젝트 구조 (DDD 패턴)
- ✅ Docker Compose 개발 환경 (PostgreSQL)
- ✅ Global 설정 (CORS, Swagger, Exception Handler)
- ✅ 공통 API Response 구조
- ✅ 개발 문서 (README, SETUP, API_GUIDE)
- ✅ GitHub 이슈 템플릿 (Bug Report, Feature Request, Question)
- ✅ GitHub Actions CI/CD 파이프라인 (자동 빌드 및 Docker Hub 배포)

### 다음 단계

1. ERD 설계 및 데이터베이스 스키마 정의
2. 도메인별 비즈니스 로직 구현
3. API 엔드포인트 개발
4. 테스트 코드 작성

## 팀원 온보딩

신규 팀원이 프로젝트에 합류했을 때 따라야 할 가이드입니다.

### 1. 레포지토리 클론

```bash
git clone https://github.com/New-Branch-dev/nb-backend.git
cd nb-backend
```

### 2. 로컬 개발 환경 설정

아래 "시작하기" 섹션의 지침을 따라 로컬 환경을 설정하세요:
- Docker & Docker Compose 설치
- `.env` 파일 생성 (`.env.example` 복사)
- PostgreSQL 컨테이너 실행
- 애플리케이션 실행 및 연결 확인

### 3. GitHub 이슈 및 PR 워크플로우

#### 이슈 생성
1. GitHub 레포지토리 → **Issues** 탭
2. **New issue** 클릭
3. 적절한 템플릿 선택:
   - 🐞 **Bug report**: 버그 제보
   - 🛠️ **Feature report**: 새 기능 구현
   - ❓ **Question report**: 질문 사항
4. 템플릿에 따라 내용 작성 후 Submit

#### 개발 프로세스
1. **이슈에서 브랜치 생성** 또는 로컬에서 브랜치 생성
   ```bash
   git checkout -b feature/기능명
   # 또는
   git checkout -b fix/버그명
   ```

2. **코드 작성 및 커밋**
   ```bash
   git add .
   git commit -m "feat: 기능 설명"
   # 커밋 메시지 컨벤션:
   # feat: 새 기능
   # fix: 버그 수정
   # docs: 문서 수정
   # refactor: 리팩토링
   # test: 테스트 추가
   # chore: 기타 변경사항
   ```

3. **원격 브랜치에 푸시**
   ```bash
   git push origin feature/기능명
   ```

4. **Pull Request 생성**
   - GitHub에서 자동으로 PR 생성 안내가 표시됨
   - 또는 레포지토리 → **Pull requests** → **New pull request**
   - 변경 사항 설명 작성
   - Reviewers 지정 (선택)

5. **자동 CI 검증**
   - PR 생성 시 GitHub Actions가 자동으로 빌드 검증
   - 빌드 성공 시 ✅ 표시, 실패 시 ❌ 표시
   - Actions 탭에서 상세 로그 확인 가능

6. **코드 리뷰 및 병합**
   - 팀원들의 리뷰 완료 후 main 브랜치에 병합
   - 병합 후 자동으로 Docker 이미지 빌드 및 Docker Hub에 배포

### 4. CI/CD 파이프라인 이해하기

#### CI (Continuous Integration) - PR 빌드 검증
- **트리거**: Pull Request 생성/업데이트 시
- **동작**:
  1. Java 17 설정
  2. Gradle 빌드 (`./gradlew clean build -x test`)
  3. 빌드 성공/실패 여부를 PR에 표시
- **목적**: main 브랜치에 병합되기 전 빌드 오류 사전 차단

#### CD (Continuous Deployment) - Docker 이미지 배포
- **트리거**: main 브랜치에 push 될 때
- **동작**:
  1. Gradle 빌드
  2. Docker 이미지 빌드 (Dockerfile 사용)
  3. Docker Hub에 이미지 푸시
     - `latest`: 최신 버전
     - `main-<커밋해시>`: 특정 커밋 버전 (롤백용)
     - `main`: 브랜치 태그
- **목적**: 배포 가능한 Docker 이미지 자동 생성

#### 워크플로우 확인
- GitHub 레포지토리 → **Actions** 탭
- 실행 중인 워크플로우 및 과거 기록 확인 가능
- 실패 시 로그를 통해 원인 파악

### 5. 개발 시 주의사항

- **환경 변수**: `.env` 파일은 Git에 커밋되지 않습니다. 팀원마다 개별 설정 필요
- **데이터베이스**: 로컬 PostgreSQL은 Docker 컨테이너로 실행됩니다
- **포트 충돌**: 8080 포트가 이미 사용 중이면 `.env`에서 `SERVER_PORT` 변경
- **빌드 캐시**: Gradle 캐시가 문제를 일으킬 경우 `./gradlew clean` 실행

### 6. 도움이 필요할 때

- **기술 문서**: [SETUP.md](./SETUP.md) - 상세한 설정 가이드
- **API 문서**: [API_GUIDE.md](./API_GUIDE.md) - 프론트엔드 협업 가이드
- **질문하기**: GitHub Issues에 ❓ Question report 템플릿으로 질문 등록

## 시작하기

### 사전 요구사항

- Docker & Docker Compose
- Java 17 (로컬 실행 시)
- Gradle 8.5+ (로컬 실행 시)

### Docker로 실행 (권장)

프로젝트 루트에서 다음 명령어를 실행하세요:

```bash
# 컨테이너 빌드 및 실행
docker-compose up -d

# 로그 확인
docker-compose logs -f app

# 컨테이너 중지
docker-compose down

# 볼륨 포함 완전 삭제
docker-compose down -v
```

### 로컬 개발 환경 설정

#### 1. 환경 변수 설정

`.env.example` 파일을 복사하여 `.env` 파일을 생성하세요:

```bash
cp .env.example .env
```

`.env` 파일은 로컬 개발 환경에서 사용되며, Git에 커밋되지 않습니다.

#### 2. PostgreSQL 실행

Docker Compose를 사용하여 PostgreSQL만 실행:

```bash
# PostgreSQL 컨테이너만 실행
docker-compose up -d postgres

# PostgreSQL 상태 확인
docker-compose ps

# PostgreSQL 로그 확인
docker-compose logs -f postgres
```

#### 3. 애플리케이션 실행

Spring Boot 애플리케이션을 로컬에서 실행:

```bash
# 애플리케이션 빌드
./gradlew build

# 애플리케이션 실행 (.env 파일 로드)
./gradlew bootRun

# 또는 프로파일 지정하여 실행
SPRING_PROFILES_ACTIVE=local ./gradlew bootRun
```

#### 4. 연결 확인

애플리케이션이 정상적으로 실행되면:

- **Health Check:** http://localhost:8080/actuator/health
- **Swagger UI:** http://localhost:8080/swagger-ui.html

#### 팁

- PostgreSQL 중지: `docker-compose stop postgres`
- PostgreSQL 재시작: `docker-compose restart postgres`
- 데이터 초기화: `docker-compose down -v` (주의: 모든 데이터 삭제)

## API 문서

애플리케이션 실행 후 다음 URL에서 API 문서를 확인할 수 있습니다:

- **Swagger UI:** http://localhost:8080/swagger-ui.html
- **OpenAPI Spec:** http://localhost:8080/api-docs

## 환경 변수

`docker-compose.yml`에서 다음 환경 변수를 설정할 수 있습니다:

| 변수명 | 기본값 | 설명 |
|--------|--------|------|
| `SPRING_DATASOURCE_URL` | `jdbc:postgresql://postgres:5432/newbranch` | 데이터베이스 URL |
| `SPRING_DATASOURCE_USERNAME` | `postgres` | 데이터베이스 사용자명 |
| `SPRING_DATASOURCE_PASSWORD` | `postgres` | 데이터베이스 비밀번호 |
| `SPRING_JPA_DDL_AUTO` | `update` | JPA DDL 정책 |
| `SERVER_PORT` | `8080` | 서버 포트 |
| `LOG_LEVEL` | `INFO` | 로그 레벨 |

## API 예시

ERD 설계 및 도메인 구현 후 API 예시가 추가될 예정입니다.

## 응답 형식

모든 API 응답은 다음 형식을 따릅니다:

### 성공 응답

```json
{
  "status": "success",
  "data": {
    // 실제 데이터
  }
}
```

### 에러 응답

```json
{
  "status": "error",
  "code": "C001",
  "message": "Error message"
}
```

## CORS 설정

기본적으로 다음 origin이 허용됩니다:

- `http://localhost:3000` (Next.js 개발 서버)
- `https://yourdomain.com` (프로덕션)

추가 origin이 필요한 경우 `CorsConfig.java`를 수정하세요.

## 배포

### 자동 배포 (GitHub Actions)

main 브랜치에 코드가 병합되면 GitHub Actions가 자동으로 Docker 이미지를 빌드하여 Docker Hub에 배포합니다.

**배포된 이미지 사용**:
```bash
# Docker Hub에서 최신 이미지 pull
docker pull <username>/newbranch-backend:latest

# 컨테이너 실행
docker run -d \
  --name newbranch-app \
  --restart unless-stopped \
  -p 8080:8080 \
  -e SPRING_DATASOURCE_URL=jdbc:postgresql://your-db-host:5432/newbranch \
  -e SPRING_DATASOURCE_USERNAME=your-username \
  -e SPRING_DATASOURCE_PASSWORD=your-password \
  -e SPRING_JPA_DDL_AUTO=update \
  -e SERVER_PORT=8080 \
  -e LOG_LEVEL=INFO \
  <username>/newbranch-backend:latest
```

### 수동 배포 (로컬 빌드)

필요한 경우 로컬에서 직접 이미지를 빌드할 수 있습니다:

```bash
# 이미지 빌드
docker build -t newbranch-backend .

# 이미지 실행
docker run -d \
  -p 8080:8080 \
  -e SPRING_DATASOURCE_URL=jdbc:postgresql://your-db-host:5432/newbranch \
  -e SPRING_DATASOURCE_USERNAME=your-username \
  -e SPRING_DATASOURCE_PASSWORD=your-password \
  newbranch-backend
```

### AWS EC2 배포 (권장)

AWS 프리티어 t2.micro 인스턴스에서 실행 가능하도록 최적화되어 있습니다.
- JVM 메모리 제한: 512MB
- Docker 이미지 크기: ~200-300MB
