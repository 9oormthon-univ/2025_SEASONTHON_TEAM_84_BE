# 멀티스테이지 빌드를 사용한 Spring Boot 애플리케이션 Dockerfile

# Stage 1: 빌드 단계
FROM gradle:8.5-jdk17 AS builder

# 작업 디렉토리 설정
WORKDIR /app

# Gradle 래퍼와 빌드 스크립트 복사
COPY gradlew .
COPY gradle gradle
COPY build.gradle .
COPY settings.gradle .

# gradlew 실행 권한 부여
RUN chmod +x ./gradlew

# 의존성 다운로드 (캐시 최적화)
RUN ./gradlew dependencies --no-daemon

# 소스 코드 복사 (변경 시 이후 레이어 자동 무효화)
COPY src src

# Git 정보 복사 (커밋 해시 기반 캐시 무효화)
COPY .git .git

# 애플리케이션 빌드 (clean으로 이전 빌드 제거)
RUN ./gradlew clean bootJar --no-daemon --no-build-cache

# Stage 2: 실행 단계
FROM eclipse-temurin:17-jre-jammy

# 필요한 패키지 설치 및 한국 시간대 설정
RUN apt-get update && apt-get install -y tzdata curl && \
    ln -sf /usr/share/zoneinfo/Asia/Seoul /etc/localtime && \
    echo "Asia/Seoul" > /etc/timezone && \
    apt-get clean && rm -rf /var/lib/apt/lists/*

# 애플리케이션 사용자 생성 (보안)
RUN groupadd -r appuser && useradd -r -g appuser appuser

# 작업 디렉토리 설정
WORKDIR /app

# 빌드된 JAR 파일 복사
COPY --from=builder /app/build/libs/*.jar app.jar

# 파일 권한 설정
RUN chown -R appuser:appuser /app
USER appuser

# 헬스체크 (Render용)
HEALTHCHECK --interval=30s --timeout=3s --start-period=40s --retries=3 \
  CMD curl -f http://localhost:${PORT:-8080}/actuator/health || exit 1

# JVM 옵션 설정
ENV JAVA_OPTS="-XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0 -Djava.security.egd=file:/dev/./urandom"

# 포트 노출 (Render에서 동적으로 할당)
EXPOSE ${PORT:-8080}

# 애플리케이션 실행
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
