# 🛒 Order Management System

**Java Spring Boot ilə hazırlanmış sifariş idarəetməsi sistemi**

Bu layihə müasir Java texnologiyaları ilə hazırlanmış, tam funksional sifariş idarəetməsi sistemidir. Sistemdə OTP ilə authentication, JWT token idarəetməsi, rol-based access control və digər müasir özelliklər mövcuddur.

---

## 📋 Xüsusiyyətlər

### 🔐 Authentication & Security
- **OTP sistemi** - SMS ilə doğrulama
- **JWT Token** - Access və refresh token-lar
- **Role-based Access Control** - Müştəri, Hazırlayan, Admin rolları
- **Spring Security** - Tam təhlükəsizlik

### 👥 İstifadəçi İdarəetməsi
- Mobil nömrə ilə qeydiyyat/giriş
- İstifadəçi profil idarəetməsi
- Rol əsaslı səlahiyyətlər
- İstifadəçi axtarışı və filtrlənməsi

### 🏗️ Texniki Xüsusiyyətlər
- **Domain-Driven Design (DDD)** struktur
- **RESTful API** - Swagger/OpenAPI sənədləri
- **PostgreSQL** - Əsas verilənlər bazası
- **RabbitMQ** - SMS və notification queue
- **Docker** - Containerization dəstəyi
- **Gradle** - Build tool

---

## 🚀 Sürətli Başlanğıc

### 📋 Tələblər

- **Java 17+** (OpenJDK tövsiyə olunur)
- **PostgreSQL 12+** 
- **RabbitMQ 3.8+** (istəyə görə)
- **Docker & Docker Compose** (istəyə görə)

### ⚡ 1 Dəqiqə İçində İşə Sal

```bash
# Layihəni klonla
git clone https://github.com/youruser/order-management-system.git
cd order-management-system

# Docker ilə bütün servislər
docker-compose up -d

# Aplikasiyanı test et
curl http://localhost:8080/actuator/health
```

### 🔧 Yerli Development

```bash
# Database quraşdır
createdb order_system_db

# Konfiqurasiya
cp gradle.properties.example gradle.properties
# gradle.properties faylında database parolunu təyin edin

# Aplikasiyanı işə sal
./gradlew bootRun

# Browser-da aç
open http://localhost:8080/swagger-ui.html
```

---

## 📱 API İstifadəsi

### 🔐 Authentication

**1. OTP Göndər:**
```bash
POST /api/v1/auth/send-otp
Content-Type: application/json

{
  "phoneNumber": "+994501234567"
}
```

**2. OTP ilə Giriş:**
```bash
POST /api/v1/auth/verify-otp
Content-Type: application/json

{
  "phoneNumber": "+994501234567",
  "otpCode": "123456"
}
```

**3. Token ilə API-ya Müraciət:**
```bash
GET /api/v1/users/profile
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```

### 👤 İstifadəçi İdarəetməsi

```bash
# Profil məlumatları
GET /api/v1/users/profile

# İstifadəçi yeniləmə  
PUT /api/v1/users/{id}
{
  "fullName": "Mübariz Əliyev",
  "email": "mubariz@example.com"
}

# İstifadəçilər siyahısı (Admin)
GET /api/v1/users?page=0&size=10&sort=createdAt,desc
```

---

## 🏗️ Layihə Strukturu

```
src/main/java/com/app/yolla/
├── OrderSystemApplication.java          # Ana application sinfi
├── config/                              # Konfiqurasiya faylları
│   ├── SecurityConfig.java
│   └── DatabaseConfig.java
├── shared/                              # Ümumi komponentlər
│   ├── dto/ApiResponse.java
│   ├── exception/GlobalExceptionHandler.java
│   └── security/JwtUtil.java
└── modules/                             # Biznes modulları
    ├── user/                            # İstifadəçi modulu
    │   ├── controller/UserController.java
    │   ├── service/UserService.java
    │   ├── repository/UserRepository.java
    │   ├── entity/User.java
    │   └── dto/UserDTO.java
    └── auth/                            # Authentication modulu
        ├── controller/AuthController.java
        ├── service/AuthService.java
        └── entity/OtpCode.java
```

---

## 🔧 Development

### 🏃‍♂️ Gradle Əmrləri

```bash
# Aplikasiyanı işə sal
./gradlew bootRun

# Build et
./gradlew build

# Testləri işlət
./gradlew test

# Test coverage
./gradlew jacocoTestReport

# Docker image yarat
./gradlew buildDockerImage
```

### 🐳 Docker İstifadəsi

```bash
# Bütün servislər (development)
docker-compose up -d

# Yalnız database və RabbitMQ
docker-compose up -d postgres rabbitmq

# Production profili
docker-compose -f docker-compose.yml -f docker-compose.prod.yml up -d

# Logları görmək
docker-compose logs -f app_yolla
```

### 🗄️ Database İdarəetməsi

```bash
# Development-da H2 console
http://localhost:8080/h2-console

# Docker-da Adminer
http://localhost:8081

# Production-da PostgreSQL
psql -h localhost -U postgres -d order_system_db
```

---

## 🌍 Environment-lər

### 🔧 Development
- **Database:** H2 in-memory
- **SMS:** Test rejimi (konsola yazır)
- **Security:** Yüngül (debug üçün)
- **Logs:** Ətraflı debug məlumatları

### 🚀 Production  
- **Database:** PostgreSQL
- **SMS:** Həqiqi SMS provayder
- **Security:** Tam təhlükəsizlik
- **Logs:** Optimized, structured

### 🧪 Test
- **Database:** H2 + Testcontainers
- **SMS:** Mock servis
- **Security:** Test konfiqurasiyası

---

## 🔑 Environment Variables

### Mütləq Tələb Olunanlar:
```bash
# Production üçün
DATABASE_PASSWORD=your_secure_password
JWT_SECRET=your_very_long_and_secure_secret_key
TWILIO_ACCOUNT_SID=your_twilio_sid
TWILIO_AUTH_TOKEN=your_twilio_token
```

### İxtiyari:
```bash
DATABASE_URL=jdbc:postgresql://localhost:5432/order_system_db
RABBITMQ_HOST=localhost
ALLOWED_ORIGINS=https://yourdomain.com
MAX_FILE_SIZE=5MB
```

---

## 📊 Monitoring & Health

### Health Checks
```bash
# Aplikasiya health
curl http://localhost:8080/actuator/health

# Database connection
curl http://localhost:8080/actuator/health/db

# RabbitMQ connection  
curl http://localhost:8080/actuator/health/rabbit
```

### Metrics
```bash
# Prometheus metrics
curl http://localhost:8080/actuator/prometheus

# Application metrics
curl http://localhost:8080/actuator/metrics
```

### API Documentation
- **Swagger UI:** http://localhost:8080/swagger-ui.html
- **OpenAPI JSON:** http://localhost:8080/v3/api-docs

---

## 🧪 Testing

```bash
# Unit testlər
./gradlew test

# Integration testlər
./gradlew integrationTest

# API testləri (Postman collection)
newman run postman/Order-System.postman_collection.json

# Performance testləri
./gradlew gatlingRun
```

---

## 🐛 Troubleshooting

### Ümumi Problemlər

**Port məşğul:**
```bash
# Port 8080 məşğul olarsa
lsof -i :8080
kill -9 <PID>
```

**Database bağlantı xətası:**
```bash
# PostgreSQL işləyib-işləmədiyini yoxla
pg_isready -h localhost -p 5432
```

**Gradle cache problemləri:**
```bash
./gradlew clean
rm -rf ~/.gradle/caches/
```

### Debug Mode

```bash
# Debug logging ilə işə sal
./gradlew bootRun --args='--logging.level.com.app.yolla=DEBUG'

# Java debug port
./gradlew bootRun --debug-jvm
```

---

## 🤝 Töhfə Vermək

1. Fork edin
2. Feature branch yaradın (`git checkout -b feature/amazing-feature`)
3. Dəyişiklikləri commit edin (`git commit -m 'Add amazing feature'`)
4. Branch-ı push edin (`git push origin feature/amazing-feature`)
5. Pull Request açın

---

## 📄 Lisenziya

Bu layihə MIT lisenziyası altındadır. Ətraflı məlumat üçün [LICENSE](LICENSE) faylına baxın.

---

## 📞 Dəstək

- **Issues:** [GitHub Issues](https://github.com/youruser/order-management-system/issues)
- **Discussions:** [GitHub Discussions](https://github.com/youruser/order-management-system/discussions)
- **Email:** support@yourcompany.com

---

**Made with ❤️ by Order System Team**