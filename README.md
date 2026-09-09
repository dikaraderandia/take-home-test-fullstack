# take-home-test

Spring Boot 3.5.5 (Java 17) fullstack product management app.

- REST API (`/api/**`) with JWT auth, rate limiting, async processing, and Redis cache
- Web UI with JSP views (`/products`)
- Swagger/OpenAPI documentation

## Prerequisites

- Java 17
- Docker (for PostgreSQL & Redis), or local installations

## How to Run Locally

### Option A: With Docker

1. **Start PostgreSQL and Redis** (Docker):

   ```bash
   docker run -d --name takehome-postgres -p 5432:5432 -e POSTGRES_USER=postgres -e POSTGRES_PASSWORD=1 postgres:16
   docker run -d --name takehome-redis -p 6379:6379 redis:7
   ```

2. **Create the database**:

   ```bash
   docker exec -it takehome-postgres createdb -U postgres test_code_id
   ```

3. **Run the app** (schema is auto-created by Hibernate):

   ```bash
   ./mvnw spring-boot:run
   ```

4. **Register an account** and get a token:

   ```bash
   curl -X POST http://localhost:8080/api/auth/register \
     -H "Content-Type: application/json" \
     -d '{"username":"admin","password":"password123","passwordConfirmation":"password123"}'

   curl -X POST http://localhost:8080/api/auth/login \
     -H "Content-Type: application/json" \
     -d '{"username":"admin","password":"password123"}'
   ```

5. **Test the API / UI**:

   - REST API (use the token from login as `Authorization: Bearer <token>`):
     - `GET/POST /api/products` — list/create products
     - `GET/PUT/DELETE /api/products/{id}` — get/update/delete a product
   - Swagger docs: http://localhost:8080/documentation-api
   - Web UI: http://localhost:8080/products

### Option B: Without Docker (native install)

1. **Install PostgreSQL and Redis** on your machine:
   - [PostgreSQL](https://www.postgresql.org/download/) (listen on port `5432`)
   - [Redis](https://redis.io/docs/latest/operate/oss_and_stack/install/install-redis/) (listen on port `6379`)

2. **Create the database** (user `postgres`, password `1` to match the config):

   ```bash
   psql -U postgres -h localhost -c "CREATE DATABASE test_code_id;"
   ```

3. **Run the app** (schema is auto-created by Hibernate):

   ```bash
   ./mvnw spring-boot:run
   ```

4. **Register an account** and get a token:

   ```bash
   curl -X POST http://localhost:8080/api/auth/register \
     -H "Content-Type: application/json" \
     -d '{"username":"admin","password":"password123","passwordConfirmation":"password123"}'

   curl -X POST http://localhost:8080/api/auth/login \
     -H "Content-Type: application/json" \
     -d '{"username":"admin","password":"password123"}'
   ```

5. **Test the API / UI**: same as Option A (Swagger: http://localhost:8080/documentation-api, Web UI: http://localhost:8080/products)

> Note: if your local PostgreSQL uses a different password, update `spring.datasource.password` in `src/main/resources/application.properties`.

## Configuration Notes

- DB connection, Redis, and JWT settings are in `src/main/resources/application.properties`
- Default DB: `localhost:5432/test_code_id` (user `postgres`, password `1`)
- Redis cache at `localhost:6379`