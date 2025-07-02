1. Docker Desktop-u işə salın
Əgər Docker Desktop aktiv deyilsə, digər əmrlər işləməyəcək.

2. Layihəni işə salmaq
docker compose up --build

3. Servisləri dayandırmaq
docker compose down

4. OTP göndərmək və təsdiqləmək

- `POST /api/auth/send-otp`
{
    "phoneNumber":"+994555045034"
}--göndərirsən və sənə otp verir.Sonra o otp ni aşağıda istifadə edirsən

- `POST /api/auth/verify-otp`
{
    "phoneNumber":"+994555045034",
    "otpCode":"570856"
}-bunu etdikden sonra sene token verir ve admin rolunda olursan.Sonra da gedib user qeydiyyatdan kecirdirsen ve apileri islede bilirsən

5. Swagger: http://localhost:8080/api/v1/swagger-ui/index.html#/

6.Docker konteynerində MySQL-ə qoşulma
MySQL konteynerinə daxil olun:
docker exec -it order-system-mysql bash

MySQL müştərisini işə salın:
mysql -u root -p
Parolu daxil edin. Parol düzgün olduqda MySQL monitoruna daxil olacaqsınız:

Welcome to the MySQL monitor. Commands end with ; or \g.
Your MySQL connection id is 35
Server version: 8.0.42 MySQL Community Server - GPL
...
mysql>

Əgər Access denied xətası alırsınızsa, docker-compose.yml faylinda MYSQL_ROOT_PASSWORD bu hisseye baxib yeniden duzeldin