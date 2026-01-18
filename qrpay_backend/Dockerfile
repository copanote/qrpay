# 1. 베이스 이미지 선택 (필수)
FROM amazoncorretto:21.0.9-al2023

# 2. 작업 디렉토리 생성 및 이동
WORKDIR /app

# 3. 빌드된 JAR 파일 복사
COPY target/app.jar app.jar

# 4. 컨테이너 내부 포트 명시 (선택사항이나 권장)
EXPOSE 9090

# 5. 애플리케이션 실행 명령 (필수)
ENTRYPOINT ["java", "-jar", "app.jar"]