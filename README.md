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

## 주요 기능

- ✅ RESTful API 설계
- ✅ Domain-Driven Design (DDD) 구조
- ✅ Global Exception Handling
- ✅ Swagger UI Documentation
- ✅ CORS 설정 (Next.js 연동)
- ✅ PostgreSQL 연동
- ✅ Docker 컨테이너화
- ✅ JPA Auditing (created_at, updated_at)

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

AWS 프리티어를 고려한 최적화된 Docker 이미지를 제공합니다:

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

## 라이선스

MIT License
