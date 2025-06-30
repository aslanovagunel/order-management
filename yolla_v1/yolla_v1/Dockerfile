# Multi-stage build üçün Dockerfile
# Bu faylı layihənin root qovluğuna qoyun

# Build stage
FROM openjdk:17-jdk-alpine AS builder

# Metadata
LABEL maintainer="Order Management System Team"
LABEL description="Java Spring Boot Sifariş İdarəetməsi Sistemi"

# Working directory
WORKDIR /app

# Gradle wrapper və build faylları kopyala
COPY gradlew .
COPY gradlew.bat .
COPY gradle gradle
COPY build.gradle .
COPY settings.gradle .
COPY gradle.properties .

# Source kodunu kopyala
COPY src src

# Gradle wrapper-ə icazə ver
RUN chmod +x ./gradlew

# Dependencies yüklə və build et
RUN ./gradlew build -x test --no-daemon

# Runtime stage
FROM openjdk:17-alpine AS runtime

# Metadata
LABEL version="1.0.0"
LABEL description="Order Management System Runtime"

# Security: non-root user yarat
RUN addgroup -g 1001 app_yolla && \
    adduser -u 1001 -G app_yolla -s /bin/sh -D app_yolla

# Working directory
WORKDIR /app

# Runtime packages quraşdır
RUN apk add --no-cache \
    curl \
    tzdata

# Timezone təyin et
ENV TZ=Asia/Baku
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone

# JAR faylını builder stage-dən kopyala
COPY --from=builder /app/build/libs/*.jar app.jar

# Ownership dəyiş
RUN chown -R app_yolla:app_yolla /app

# User switch
USER app_yolla

# Health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=30s --retries=3 \
    CMD curl -f http://localhost:8080/actuator/health || exit 1

# Port expose et
EXPOSE 8080

# JVM options
ENV JAVA_OPTS="-Xmx1024m -Xms512m -XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0"

# Aplikasiyanı işə sal
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]

# Development üçün alternativ Dockerfile
# ------------------
# FROM openjdk:17-jdk-alpine AS development
#
# WORKDIR /app
#
# # Development dependencies
# RUN apk add --no-cache curl vim
#
# # Gradle wrapper
# COPY gradlew .
# COPY gradle gradle
# RUN chmod +x ./gradlew
#
# # Build faylları
# COPY build.gradle .
# COPY settings.gradle .
# COPY gradle.properties .
#
# # Dependencies yüklə
# RUN ./gradlew dependencies --no-daemon
#
# # Source mount point
# VOLUME ["/app/src"]
#
# # Development mode
# CMD ["./gradlew", "bootRun", "--continuous"]