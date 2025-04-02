# Auth Service

Spring Boot mikroservisi ile JWT kimlik doğrulama, Keycloak entegrasyonu, OpenFGA yetkilendirme ve cihaz takibi.

## Proje Hakkında

Bu mikroservis, Java 17 ve Spring Boot kullanılarak geliştirilmiş olup, JWT tabanlı kimlik doğrulama, Keycloak entegrasyonu, OpenFGA ile ince taneli yetkilendirme ve cihaz MAC ID takibi özelliklerine sahiptir. Servis, Eureka üzerinden kayıt olur ve API Gateway üzerinden istekleri alır.

## Teknolojiler

- Java 17
- Spring Boot 3.1.5
- Spring Security
- Spring Cloud (Eureka Client)
- Keycloak 22.0.1
- OpenFGA
- PostgreSQL
- Gradle
- JUnit 5 & Testcontainers
- Swagger/OpenAPI

## Özellikler

- JWT tabanlı kimlik doğrulama
- Harici Keycloak entegrasyonu
- OpenFGA ile ince taneli yetkilendirme (endpoint bazında ve veri filtreleme)
- Cihaz yönetimi (MAC ID takibi, cihaz engelleme)
- Eureka servis kaydı
- API Gateway entegrasyonu
- PostgreSQL veritabanı entegrasyonu
- Kapsamlı birim ve entegrasyon testleri
- Swagger/OpenAPI dokümantasyonu

## Kurulum

### Ön Koşullar

- Java 17 veya üzeri
- PostgreSQL
- Keycloak sunucusu
- OpenFGA sunucusu
- Eureka sunucusu
- API Gateway

### Veritabanı Kurulumu

PostgreSQL veritabanını oluşturun:

```sql
CREATE DATABASE auth_service_db;
CREATE USER auth_service WITH PASSWORD 'your_password';
GRANT ALL PRIVILEGES ON DATABASE auth_service_db TO auth_service;
```

### Yapılandırma

`application.yml` dosyasını kendi ortamınıza göre düzenleyin:

```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/auth_service_db
    username: auth_service
    password: your_password

  security:
    oauth2:
      resourceserver:
        jwt:
          issuer-uri: http://your-keycloak-server/auth/realms/your-realm

keycloak:
  auth-server-url: http://your-keycloak-server/auth
  realm: your-realm
  resource: your-client-id
  
openfga:
  api-url: http://your-openfga-server
  store-id: your-store-id
  
eureka:
  client:
    service-url:
      defaultZone: http://your-eureka-server/eureka/
```

### Derleme ve Çalıştırma

```bash
./gradlew clean build
java -jar build/libs/auth-service-0.0.1-SNAPSHOT.jar
```

## API Endpoints

### Kimlik Doğrulama

- `POST /api/auth/register`: Yeni kullanıcı kaydı
- `POST /api/auth/device`: Cihaz kaydı
- `POST /api/auth/device/{deviceId}/block`: Cihaz engelleme
- `POST /api/auth/device/{deviceId}/unblock`: Cihaz engeli kaldırma
- `POST /api/auth/logout`: Çıkış yapma ve token'ı geçersiz kılma

### Cihaz Yönetimi

- `GET /api/devices/user/{userId}`: Kullanıcının cihazlarını getirme
- `POST /api/devices`: Cihaz kaydı
- `POST /api/devices/{deviceId}/block`: Cihaz engelleme
- `POST /api/devices/{deviceId}/unblock`: Cihaz engeli kaldırma
- `GET /api/devices/search`: MAC adresi ile cihaz arama

### Yetkilendirme

- `POST /api/authorization/check`: Yetkilendirme kontrolü
- `POST /api/authorization/grant`: Yetki verme
- `POST /api/authorization/revoke`: Yetki kaldırma
- `GET /api/authorization/user/{userId}`: Kullanıcı yetkilerini getirme
- `GET /api/authorization/object`: Nesne yetkilerini getirme

## Güvenlik

Servis, JWT token tabanlı kimlik doğrulama kullanır. Tüm API istekleri (kimlik doğrulama ve Swagger dokümantasyonu hariç) için geçerli bir JWT token gereklidir. Token, `Authorization` header'ında `Bearer {token}` formatında gönderilmelidir.

Cihaz MAC adresi, `X-MAC-Address` header'ında gönderilmelidir.

## Testler

Servis, kapsamlı birim ve entegrasyon testleri içerir. Testleri çalıştırmak için:

```bash
./gradlew test
```

## Dokümantasyon

API dokümantasyonu, Swagger UI aracılığıyla erişilebilir:

```
http://localhost:8081/auth-service/swagger-ui.html
```

## Lisans

Bu proje [Apache License 2.0](LICENSE) altında lisanslanmıştır.
