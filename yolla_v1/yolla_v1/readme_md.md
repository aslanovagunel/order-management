# 📦 Order Management System

Bu layihə **sifarişlərin idarə olunması** üçün hazırlanmış sadə və funksional bir **RESTful API** sistemidir. Backend hissəsi `Java` və `Spring Boot` texnologiyaları ilə yazılıb. Layihə həm **lokal mühitdə**, həm də **Docker konteynerləri** ilə asanlıqla işlədilə bilər. İstifadə rahatlığı üçün `Postman Collection` da əlavə edilib.

---

## 🚀 Texnologiyalar
Bu layihənin əsas texnologiyaları:

- ✅ Java 17
- ✅ Spring Boot
- ✅ Spring Data JPA
- ✅ MySQL / H2 Database
- ✅ Docker & Docker Compose
- ✅ Gradle
- ✅ Postman


## 📁 Layihə Strukturu

src/main/java/com/app/yolla/
├── OrderSystemApplication.java        # 🚀 Spring Boot tətbiqinin giriş nöqtəsi (main class)
│
├── config/                            # ⚙️ Layihə konfiqurasiya faylları
│   ├── SecurityConfig.java            # Spring Security ayarları, JWT filter-lər və s.
│   └── DatabaseConfig.java            # Datasource və JPA konfiqurasiyası
│
├── shared/                            # 🌍 Layihə üzrə ümumi istifadədə olan komponentlər
│   ├── dto/
│   │   └── ApiResponse.java           # API nəticələrinin standart formatı (success, message, data)
│   ├── exception/
│   │   ├── GlobalExceptionHandler.java # Bütün exception-ların tutulduğu global handler
│   │   ├── CustomException.java       # Custom exception bazası (əlavə edilə bilər)
│   │   └── NotFoundException.java     # Məsələn, user/product tapılmayanda atıla bilər
│   └── security/
│       └── JwtUtil.java               # JWT yaratmaq, yoxlamaq, claim-ləri oxumaq və s.
│
└── modules/                           # 🧩 Modul əsaslı biznes qat
    ├── user/                          # 👥 İstifadəçi modulu
    │   ├── controller/
    │   │   └── UserController.java    # REST endpoint-lər – user CRUD, search, profile və s.
    │   ├── service/
    │   │   └── UserService.java       # Biznes məntiq – user yaradılması, dəyişməsi və s.
    │   ├── repository/
    │   │   └── UserRepository.java    # Spring Data JPA ilə işləyir (findByPhone və s.)
    │   ├── entity/
    │   │   └── User.java              # JPA @Entity – istifadəçi məlumatları
    │   └── dto/
    │       └── UserDTO.java           # User üçün request/response formatları
    │
    ├── auth/                          # 🔐 Auth modulu
    │   ├── controller/
    │   │   └── AuthController.java    # Giriş, qeydiyyat, refresh token endpoint-ləri
    │   ├── service/
    │   │   └── AuthService.java       # OTP yoxlaması, JWT generasiyası və s.
    │   └── entity/
    │       └── OtpCode.java           # OTP-lər üçün JPA entity (mobil nömrə, kod, expiry və s.)
    │
    ├── product/                       # 🛍️ Məhsul modulu 
    │   ├── controller/ProductController.java
    │   ├── service/ProductService.java
    │   ├── repository/ProductRepository.java
    │   ├── entity/Product.java
    │   └── dto/ProductDTO.java
    │
    └── order/                         # 📦 Sifariş modulu 
        ├── controller/OrderController.java
        ├── service/OrderService.java
        ├── repository/OrderRepository.java
        ├── entity/Order.java
        └── dto/OrderDTO.java


⚙️ Qurulum Təlimatı
Bu bölmə layihəni kompüterində necə işə salmağın yollarını izah edir. Aşağıdakı addımları izləyərək sistemi lokal mühitində işlədə bilərsən.
Əvvəlcə bu layihəni GitHub-dan klonla:

```bash
git clone https://github.com/aslanovagunel/order-management.git
cd order-management

Əgər kompüterində Docker və Docker Compose quruludursa, terminalda aşağıdakı əmri yaz:
docker run -p 8080:8080 -d aslanovagunel/order-management:v1

Əgər Docker istifadə etmirsənsə, aşağıdakı addımları izləyərək layihəni IDE-də run edə bilərsən:
Java 17 versiyasının quraşdırıldığından əmin ol.
Layihəni IntelliJ IDEA və ya Eclipse ilə aç.
Faylların içindəki application.properties və ya application.yml faylında verilənlər bazasını (H2 və ya MySQL) konfiqurasiya et.
spring.datasource.url=jdbc:postgresql://localhost:5432/order_db
spring.datasource.username=postgres
spring.datasource.password=1234
spring.jpa.hibernate.ddl-auto=update
OrderManagementApplication.java faylını run et.
Server http://localhost:8080 ünvanında işləməyə başlayacaq.

Layihə ilə birlikdə gələn postman/order-management.postman_collection.json faylını Postman proqramına import edərək API-ləri test edə bilərsən.
Postman collection bu əməliyyatları əhatə edir:
Yeni user əlavə etmək
Login olmaq
Yeni sifariş əlavə etmək
Yeni mehsul əlavə etmək
Sifarişləri siyahı şəklində görmək


📋 Xüsusiyyətlər

🔐 Authentication və Təhlükəsizlik
✅ Mobil nömrə və OTP (SMS) ilə doğrulama
✅ JWT Token (access və refresh token dəstəyi)
✅ Spring Security ilə role-based authorization
✅ Role əsaslı giriş nəzarəti – Müştəri, Hazırlayan (Processor), Admin
✅ Token refresh mexanizmi
✅ İstifadəçi bloklama/silmə funksiyası (əgər varsa)

👥 İstifadəçi İdarəetməsi
📱 Mobil nömrə ilə qeydiyyat və giriş
👤 İstifadəçi məlumatlarının görüntülənməsi və redaktəsi
🧑‍💼 Rolların dəyişdirilməsi və hüquqların idarəsi
🔍 İstifadəçilərin axtarışı və filtr olunması

📦 Məhsul (Product) Modulu
➕ Yeni məhsul əlavə etmək (ad, təsvir, qiymət və s.)
🔒 Məhsul funksiyalarına yalnız səlahiyyətli rolların çıxışı

🧾 Sifariş (Order) Modulu
🛒 Müştəri tərəfindən sifarişlərin yaradılması
🔁 Sifariş statuslarının izlənməsi (Pending, Preparing, Done və s.)
📦 Hər sifarişdə birdən çox məhsul seçə bilmək

🏗️ Texniki Xüsusiyyətlər
📐 Domain-Driven Design (DDD) əsaslı layihə strukturu
🌐 RESTful API + Swagger UI / OpenAPI sənədləşməsi
🐘 PostgreSQL verilənlər bazası
📩 RabbitMQ üzərindən SMS/notification növbəsi (queue)
🐳 Docker və Docker Compose dəstəyi
🔧 Gradle – build və dependency idarəetməsi
🧪 JUnit və/və ya Testcontainers ilə test mühiti 

📡 API İstifadəsi (Nümunələr)

1. 🔐 OTP ilə Giriş və Token Əldə Etmək

**Endpoint:** `POST /api/v1/auth/send-otp`  
**Təsvir:** Mobil nömrəyə OTP göndərir.

```json
Request:
POST /api/v1/auth/send-otp
Content-Type: application/json

{
  "phoneNumber": "+994501234567"
}

Endpoint: POST /api/v1/auth/verify-otp
Təsvir: OTP kodunu təsdiqləyir və access + refresh token qaytarır.

Request:
POST /api/v1/auth/verify-otp
Content-Type: application/json

{
  "phoneNumber": "+994501234567",
  "otpCode": "123456"
}
Response:
{
  "accessToken": "eyJhbGciOiJIUzI1NiIs...",
  "refreshToken": "eyJhbGciOiJIUzI1NiIs..."
}

2.Məhsul əlavə etmək
POST http://localhost:8080/api/v1/products
Content-Type: application/json
Authorization: Bearer <token>
{
  "name": "canta",
  "description": "tez gelsin",
  "price": 19.3,
  "stockQuantity": 3
}

3. Sifariş əlavə etmək
POST /api/orders
Headers:

http
Content-Type: application/json
Authorization: Bearer <token>  (əgər autentifikasiya varsa)
Body:
{
  "totalAmount": 150.75,
  "status": "PENDING",
  "notes": "Tez çatdırılma",
  "items": [
    {
      "productId": 1,
      "quantity": 1
    },
    {
      "productId": 2,
      "quantity": 2
    }
  ]
}

Sifarişin siyahısını gətirmək
GET http://localhost:8080/api/v1/order-item/{orderId}/begin/{begin}/length/{length}


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

