# 빌드 스테이지 (표준 이미지 사용)
FROM gradle:8.5-jdk17 AS builder
WORKDIR /app
COPY . .
# 실행 권한 부여 및 빌드
RUN chmod +x ./gradlew
# fix: 테스트는 CI 파이프라인의 별도 test 단계(Postgres/Redis 서비스 컨테이너 필요)에서 게이트하고,
# 이미지 빌드 단계는 네트워크로 붙는 DB가 없는 격리된 환경이라 여기서는 계속 제외 (siwon)
RUN ./gradlew clean build -x test --no-daemon

# 실행 스테이지
FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=builder /app/build/libs/*.jar app.jar
EXPOSE 8080
# fix: docker-compose의 컨테이너 메모리 상한(550m)보다 -Xmx가 크면 메타스페이스/스레드 스택 등
# 힙 외 영역까지 합쳐 cgroup OOM killer에 죽을 수 있어 여유를 두고 낮춤 (siwon)
ENTRYPOINT ["java", "-Xmx384m", "-XX:MaxMetaspaceSize=128m", "-jar", "app.jar"]