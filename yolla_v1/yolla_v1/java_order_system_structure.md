# Java Spring Sifariş Sistemi - Layihə Strukturu

## Layihə Qovluq Strukturu

```
src/main/java/com/app/yolla/
├── config/                    # Konfiqurasiya faylları
│   ├── SecurityConfig.java
│   ├── DatabaseConfig.java
│   └── RabbitConfig.java
├── shared/                    # Ümumi komponentlər
│   ├── dto/
│   ├── exception/
│   ├── security/
│   └── util/
└── modules/                   # Əsas biznes modulları
    ├── user/                  # İstifadəçi modulü
    │   ├── controller/
    │   ├── service/
    │   ├── repository/
    │   ├── entity/
    │   └── dto/
    ├── product/               # Məhsul modulü
    │   ├── controller/
    │   ├── service/
    │   ├── repository/
    │   ├── entity/
    │   └── dto/
    ├── order/                 # Sifariş modulü
    │   ├── controller/
    │   ├── service/
    │   ├── repository/
    │   ├── entity/
    │   └── dto/
    ├── address/               # Ünvan modulü
    │   ├── controller/
    │   ├── service/
    │   ├── repository/
    │   ├── entity/
    │   └── dto/
    └── payment/               # Ödəniş modulü
        ├── controller/
        ├── service/
        ├── repository/
        ├── entity/
        └── dto/
```

## Verilənlər Bazası Strukturu

### 1. users cədvəli
```sql
CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    phone_number VARCHAR(20) UNIQUE NOT NULL,
    full_name VARCHAR(255),
    email VARCHAR(255),
    role VARCHAR(50) NOT NULL DEFAULT 'CUSTOMER',
    is_active BOOLEAN DEFAULT true,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

### 2. otp_codes cədvəli
```sql
CREATE TABLE otp_codes (
    id BIGSERIAL PRIMARY KEY,
    phone_number VARCHAR(20) NOT NULL,
    otp_code VARCHAR(6) NOT NULL,
    expires_at TIMESTAMP NOT NULL,
    is_used BOOLEAN DEFAULT false,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

### 3. products cədvəli
```sql
CREATE TABLE products (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    barcode VARCHAR(255) UNIQUE,
    qr_code VARCHAR(255) UNIQUE,
    price DECIMAL(10,2) NOT NULL,
    stock_quantity INTEGER DEFAULT 0,
    is_active BOOLEAN DEFAULT true,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

### 4. addresses cədvəli
```sql
CREATE TABLE addresses (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT REFERENCES users(id),
    title VARCHAR(100),
    full_address TEXT NOT NULL,
    city VARCHAR(100),
    district VARCHAR(100),
    is_default BOOLEAN DEFAULT false,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

### 5. orders cədvəli
```sql
CREATE TABLE orders (
    id BIGSERIAL PRIMARY KEY,
    customer_id BIGINT REFERENCES users(id),
    preparer_id BIGINT REFERENCES users(id),
    delivery_address_id BIGINT REFERENCES addresses(id),
    total_amount DECIMAL(10,2) NOT NULL,
    status VARCHAR(50) DEFAULT 'PENDING',
    payment_method VARCHAR(50),
    payment_status VARCHAR(50) DEFAULT 'PENDING',
    delivery_date DATE,
    delivery_time_start TIME,
    delivery_time_end TIME,
    notes TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

### 6. order_items cədvəli
```sql
CREATE TABLE order_items (
    id BIGSERIAL PRIMARY KEY,
    order_id BIGINT REFERENCES orders(id),
    product_id BIGINT REFERENCES products(id),
    quantity INTEGER NOT NULL,
    unit_price DECIMAL(10,2) NOT NULL,
    total_price DECIMAL(10,2) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

### 7. order_recipients cədvəli (Sifarişi qəbul edənlər)
```sql
CREATE TABLE order_recipients (
    id BIGSERIAL PRIMARY KEY,
    order_id BIGINT REFERENCES orders(id),
    recipient_name VARCHAR(255) NOT NULL,
    recipient_phone VARCHAR(20) NOT NULL,
    relationship VARCHAR(100),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

## Təhlükəsizlik Xüsusiyyətləri

1. **JWT Token Authentication**
2. **OTP doğrulama**
3. **Rate Limiting**
4. **Input Validation**
5. **SQL Injection qorunması**
6. **CORS konfiqurasiyası**
7. **Password encoding (gələcək üçün)**

## Texnologiyalar

- **Java 17+**
- **Spring Boot 3.x**
- **Spring Security**
- **Spring Data JPA**
- **PostgreSQL**
- **RabbitMQ**
- **JWT**
- **Validation API**

## Növbəti Addımlar

1. Əsas konfiqurasiya faylları
2. User modulunun yaradılması
3. OTP sisteminin qurulması
4. Product modulunun yaradılması
5. Order modulunun yaradılması
6. Payment sisteminin inteqrasiyası
7. Security konfiqurasiyası