Linkito - Backend

Backend Spring Boot do Linkito, uma plataforma de encurtamento de URLs com autenticacao JWT, redirecionamento publico, registro de cliques, limite simples de requisicoes e endpoints de estatisticas.

## Stack

- Java 21
- Spring Boot 3
- Spring Web
- Spring Security
- Spring Data JPA
- PostgreSQL
- Flyway
- JWT com JJWT
- BCrypt
- JUnit e Mockito

## Endpoints principais

### Autenticacao

- `POST /api/auth/register`
- `POST /api/auth/login`
- `GET /api/auth/me`

### Links

- `POST /api/links`
- `GET /api/links`
- `GET /api/links/{id}`
- `PUT /api/links/{id}`
- `DELETE /api/links/{id}`

### Redirecionamento

- `GET /r/{codigoCurto}`

### Estatisticas

- `GET /api/stats/dashboard`
- `GET /api/stats/links/{id}`

## Rodando com Docker

Na pasta raiz do projeto:

```bash
docker compose up --build
```

O backend sobe em `http://localhost:8080`.

## Rodando localmente

Configure um PostgreSQL local ou use as variaveis de ambiente:

```bash
SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/linkito
SPRING_DATASOURCE_USERNAME=postgres
SPRING_DATASOURCE_PASSWORD=postgres
JWT_SEGREDO=changeit-changeit-changeit-changeit-changeit
JWT_EXPIRACAO=3600000
```

Depois rode:

```bash
./mvnw spring-boot:run
```

No Windows:

```bash
.\mvnw.cmd spring-boot:run
```

## Testes

```bash
./mvnw test
```

No Windows:

```bash
.\mvnw.cmd test
```
