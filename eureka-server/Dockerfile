# Dockerfile này đặt trong từng thư mục service
# Ví dụ: user-service/Dockerfile, eureka-server/Dockerfile, v.v.

FROM eclipse-temurin:21-jdk-alpine

# Tạo thư mục làm việc
WORKDIR /app

# Copy file jar từ thư mục target
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} app.jar

# Tạo user không phải root (bảo mật)
RUN addgroup -g 1001 -S appgroup && \
    adduser -u 1001 -S appuser -G appgroup
USER appuser

EXPOSE 8761

# Health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=60s --retries=3 \
  CMD curl -f http://localhost:8080/actuator/health || exit 1

# Chạy ứng dụng
ENTRYPOINT ["java", "-Xmx512m", "-Xms256m", "-jar", "/app/app.jar"]