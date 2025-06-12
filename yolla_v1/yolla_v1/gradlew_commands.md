# Gradle Commands - Sifariş İdarəetməsi Sistemi

## 🚀 Əsas Gradle Əmrləri

### Aplikasiyanı işə salmaq
```bash
# Development rejimində işə sal
./gradlew bootRun

# Development profili ilə (xüsusi task)
./gradlew runDev

# Production profili ilə
./gradlew runProd

# Windows-da
gradlew.bat bootRun
```

### Build əməliyyatları
```bash
# Sadə build
./gradlew build

# Təmiz build
./gradlew clean build

# Test olmadan build
./gradlew build -x test

# JAR fayl yaratmaq
./gradlew bootJar
```

### Test əməliyyatları
```bash
# Bütün testləri işlət
./gradlew test

# Test coverage report
./gradlew jacocoTestReport

# Tam test suite
./gradlew fullTest

# Xüsusi test sinfi
./gradlew test --tests "*UserServiceTest"
```

### Database əməliyyatları
```bash
# Database migration (gələcək üçün)
./gradlew migrateDatabase

# Test database setup
./gradlew testClasses
```

### Development əməliyyatları
```bash
# Dependencies yoxla
./gradlew dependencies

# Project struktur
./gradlew projects

# Task-ları göstər
./gradlew tasks

# Build məlumatları
./gradlew printBuildInfo
```

### Docker əməliyyatları
```bash
# Docker image yarat
./gradlew buildDockerImage

# JAR yaradıb Docker build et
./gradlew bootJar buildDockerImage
```

### Code Quality
```bash
# Kod stilini yoxla
./gradlew checkCodeStyle

# Bütün quality checks
./gradlew check
```

## 📁 Faylların Yerləşdirilməsi

### Ana qovluq strukturu:
```
project-root/
├── build.gradle                 # Ana build fayl
├── settings.gradle              # Gradle settings
├── gradle.properties            # Konfiqurasiya
├── gradlew                      # Gradle wrapper (Linux/Mac)
├── gradlew.bat                  # Gradle wrapper (Windows)
├── gradle/
│   └── wrapper/
│       ├── gradle-wrapper.jar
│       └── gradle-wrapper.properties
└── src/
    ├── main/
    │   ├── java/
    │   └── resources/
    └── test/
        ├── java/
        └── resources/
```

## 🔧 İlk dəfə quraşdırma

### 1. Java 17+ quraşdırın
```bash
java -version
# OpenJDK 17 və ya daha yeni versiya olmalıdır
```

### 2. PostgreSQL quraşdırın və verilənlər bazası yaradın
```sql
CREATE DATABASE order_system_db;
CREATE USER postgres WITH PASSWORD 'your_password_here';
GRANT ALL PRIVILEGES ON DATABASE order_system_db TO postgres;
```

### 3. Layihəni klonlayın və işə salın
```bash
# Dependencies yüklə
./gradlew build

# Aplikasiyanı işə sal
./gradlew bootRun
```

### 4. RabbitMQ quraşdırın (SMS üçün)
```bash
# Docker ilə
docker run -d --name rabbitmq -p 5672:5672 -p 15672:15672 rabbitmq:3-management

# Və ya yerli quraşdırma
# https://www.rabbitmq.com/download.html
```

## 🌍 Profillər

### Development (dev)
- H2 in-memory database
- Debug logging aktiv
- Auto-reload aktiv
- Test SMS servisi

### Production (prod)  
- PostgreSQL database
- Optimized logging
- Həqiqi SMS servisi
- Security enhanced

### Test
- H2 test database
- Test containers
- Mock services

## 📊 Performance Monitoring

### Actuator endpoints (development):
- http://localhost:8080/actuator/health
- http://localhost:8080/actuator/metrics
- http://localhost:8080/actuator/info

### API Documentation:
- http://localhost:8080/swagger-ui.html
- http://localhost:8080/v3/api-docs

## 🐛 Debug və Troubleshooting

### Gradle Cache təmizləmək:
```bash
./gradlew clean
rm -rf ~/.gradle/caches/
```

### Dependency konfliktləri:
```bash
./gradlew dependencies --configuration compileClasspath
```

### Xəta logları:
```bash
./gradlew bootRun --debug
```

### Port məşğul olarsa:
```bash
# Port 8080 axtarmaq
netstat -tulpn | grep :8080

# Proses öldürmək  
kill -9 <PID>
```