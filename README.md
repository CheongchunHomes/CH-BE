# 청춘홈즈

청년들이 전체 구조를 쉽게 이해하고 필요한 정보를 단계적으로 탐색할 수 있도록 돕는 원스탑 청약 안내 서비스입니다.

## Tech Stack

- Java 17
- Spring Boot 4.0.5
- Spring Data JPA
- Spring Security
- Thymeleaf
- PostgreSQL
- Lombok

## Project Structure

- `src/main/java/com/chcorp/homes`
  - 애플리케이션 시작점
  - 공통 엔티티 / 보안 설정
  - `users` 도메인: controller, service, repository, entity, dto
- `src/main/resources/application.yaml`
  - 서버 포트 및 DB 설정
- `docker-compose.yml`
  - PostgreSQL 로컬 실행용 구성

## Prerequisites

- JDK 17
- Docker and Docker Compose
- PostgreSQL 17 이상 권장

## Environment Variables
아래 환경 변수가 필요합니다.

``` .env
DB_HOST=localhost
DB_PORT=5432
DB_NAME=yourDBName
DB_USERNAME=yourName
DB_PASSWORD=yourPW
```
