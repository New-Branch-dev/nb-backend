# 로컬 개발 환경 설정 가이드

이 가이드는 팀원들이 로컬 환경에서 개발을 시작하기 위한 단계별 설정 방법을 안내합니다.

## 사전 준비

다음 프로그램이 설치되어 있어야 합니다:

- **Docker Desktop** (Mac/Windows) 또는 Docker Engine (Linux)
- **Java 17** (OpenJDK 또는 Oracle JDK)
- **Git**

### Java 17 설치 (Mac)

```bash
# Homebrew를 사용한 설치
brew install openjdk@17

# PATH 설정
echo 'export PATH="/opt/homebrew/opt/openjdk@17/bin:$PATH"' >> ~/.zshrc
source ~/.zshrc

# 설치 확인
java -version
```

### Docker Desktop 설치

- Mac: https://www.docker.com/products/docker-desktop
- 설치 후 Docker Desktop을 실행하고 로그인

## 프로젝트 클론

```bash
# 프로젝트 클론
git clone <repository-url>
cd nb-backend
```

## 단계별 설정

### 1단계: 환경 변수 설정

`.env.example` 파일을 복사하여 `.env` 파일을 생성합니다:

```bash
cp .env.example .env
```

기본 설정은 로컬 개발에 적합하게 구성되어 있으므로 별도 수정 없이 사용 가능합니다.

### 2단계: PostgreSQL 실행

Docker Compose를 사용하여 PostgreSQL 데이터베이스를 실행합니다:

```bash
# PostgreSQL 컨테이너 실행
docker-compose up -d postgres

# 실행 확인 (STATUS가 "Up"이고 "healthy"로 표시되어야 함)
docker-compose ps
```

**예상 출력:**
```
NAME                 STATUS
newbranch-postgres   Up (healthy)
```

### 3단계: PostgreSQL 연결 테스트

데이터베이스가 정상적으로 실행되는지 확인합니다:

```bash
# PostgreSQL 접속 테스트
docker exec newbranch-postgres psql -U postgres -d newbranch -c "SELECT 1;"
```

**예상 출력:**
```
 ?column?
----------
        1
(1 row)
```

### 4단계: 애플리케이션 빌드

```bash
# 프로젝트 빌드
./gradlew build
```

빌드가 성공하면 `BUILD SUCCESSFUL` 메시지가 표시됩니다.

### 5단계: 애플리케이션 실행

```bash
# Spring Boot 애플리케이션 실행
./gradlew bootRun
```

애플리케이션이 정상적으로 실행되면 다음과 같은 로그가 표시됩니다:

```
Started BackendApplication in X.XXX seconds
```

### 6단계: 동작 확인

브라우저나 curl을 사용하여 API가 정상 작동하는지 확인합니다:

```bash
# Health Check
curl http://localhost:8080/actuator/health

# Swagger UI (브라우저에서 열기)
open http://localhost:8080/swagger-ui.html
```

## 자주 사용하는 명령어

### PostgreSQL 관리

```bash
# PostgreSQL 시작
docker-compose up -d postgres

# PostgreSQL 중지
docker-compose stop postgres

# PostgreSQL 재시작
docker-compose restart postgres

# PostgreSQL 로그 확인
docker-compose logs -f postgres

# PostgreSQL CLI 접속
docker exec -it newbranch-postgres psql -U postgres -d newbranch

# 데이터베이스 초기화 (주의: 모든 데이터 삭제!)
docker-compose down -v
docker-compose up -d postgres
```

### Spring Boot 애플리케이션

```bash
# 빌드 (테스트 제외)
./gradlew build -x test

# 빌드 (테스트 포함)
./gradlew build

# 실행
./gradlew bootRun

# 클린 빌드
./gradlew clean build

# 특정 프로파일로 실행
SPRING_PROFILES_ACTIVE=local ./gradlew bootRun
```

## 트러블슈팅

### PostgreSQL 포트 충돌 (5432)

이미 PostgreSQL이 로컬에 설치되어 5432 포트를 사용 중인 경우:

**방법 1: 로컬 PostgreSQL 중지**
```bash
# Mac (Homebrew로 설치한 경우)
brew services stop postgresql

# Mac (일반 설치)
sudo pkill -u postgres
```

**방법 2: Docker 포트 변경**
`docker-compose.yml` 파일에서 포트를 변경:
```yaml
ports:
  - "5433:5432"  # 5432 대신 5433 사용
```

그리고 `.env` 파일도 수정:
```env
SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5433/newbranch
```

### Docker가 실행되지 않음

```bash
# Docker Desktop이 실행 중인지 확인
docker ps

# Docker Desktop을 재시작
```

### 빌드 실패

```bash
# Gradle 캐시 정리
./gradlew clean

# Gradle Wrapper 다시 다운로드
./gradlew wrapper --gradle-version 8.5
```

### 애플리케이션이 DB에 연결되지 않음

1. PostgreSQL이 실행 중인지 확인: `docker-compose ps`
2. `.env` 파일의 DB 연결 정보가 올바른지 확인
3. PostgreSQL 로그 확인: `docker-compose logs postgres`

## IDE 설정

### IntelliJ IDEA

1. **프로젝트 열기**: `File > Open` → `nb-backend` 폴더 선택
2. **Gradle 동기화**: 자동으로 의존성을 다운로드합니다
3. **SDK 설정**: `File > Project Structure > Project` → SDK를 Java 17로 설정
4. **EnvFile 플러그인 설치** (선택사항):
   - `Settings > Plugins` → "EnvFile" 검색 및 설치
   - Run Configuration에서 `.env` 파일 자동 로드 가능

### VS Code

1. **확장 프로그램 설치**:
   - Extension Pack for Java
   - Spring Boot Extension Pack
   - Docker

2. **`.vscode/launch.json` 생성** (선택사항):
```json
{
  "version": "0.2.0",
  "configurations": [
    {
      "type": "java",
      "name": "Spring Boot",
      "request": "launch",
      "mainClass": "com.newbranch.backend.BackendApplication",
      "envFile": "${workspaceFolder}/.env"
    }
  ]
}
```

## 협업 팁

### 코드 작성 전

```bash
# 최신 코드 가져오기
git pull origin main

# 새 브랜치 생성
git checkout -b feature/your-feature-name
```

### 코드 작성 후

```bash
# 변경사항 확인
git status

# 스테이징
git add .

# 커밋
git commit -m "feat: 기능 설명"

# 푸시
git push origin feature/your-feature-name
```

### 주의사항

- ⚠️ **절대로 `.env` 파일을 커밋하지 마세요!** (이미 .gitignore에 포함됨)
- 데이터베이스 비밀번호나 API 키 같은 민감한 정보는 `.env` 파일에만 보관
- `.env.example`은 실제 값 없이 변수 이름만 포함하여 커밋 가능

## 추가 리소스

- [Spring Boot 공식 문서](https://spring.io/projects/spring-boot)
- [PostgreSQL 공식 문서](https://www.postgresql.org/docs/)
- [Docker 공식 문서](https://docs.docker.com/)
- [Gradle 공식 문서](https://docs.gradle.org/)

## 도움이 필요하신가요?

문제가 발생하거나 질문이 있으시면:

1. 먼저 이 문서의 트러블슈팅 섹션을 확인하세요
2. 팀 Slack 채널에 질문을 올려주세요
3. GitHub Issues에 버그 리포트를 작성해주세요
