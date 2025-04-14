# 기반 이미지: Java 17 사용
FROM openjdk:17-jdk-slim

RUN apt-get update && apt-get install -y curl && rm -rf /var/lib/apt/lists/*

# 작업 디렉토리 설정
WORKDIR /app

# JAR 파일 복사
COPY build/libs/mmc-0.0.1-SNAPSHOT.jar app.jar

# 실행 명령어
ENTRYPOINT ["java", "-jar", "app.jar"]

# 포트 노출d
EXPOSE 8080