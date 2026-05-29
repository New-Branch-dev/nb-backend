# 빌드 스테이지 (표준 이미지 사용)
FROM gradle:8.5-jdk17 AS builder
WORKDIR /app
COPY . .

RUN sed -i 's/\r$//' gradlew

# 실행 권한 부여 및 빌드
RUN chmod +x ./gradlew
RUN ./gradlew clean build -x test --no-daemon

# 실행 스테이지
FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=builder /app/build/libs/*-SNAPSHOT.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-Xmx512m", "-jar", "app.jar"]