# Linkito

Linkito e um encurtador de URLs fullstack criado como projeto de portfolio junior. Ele inclui autenticacao, gerenciamento de links encurtados, redirecionamento publico, registro de cliques e estatisticas basicas.

> README inicial. Screenshots, GIFs, URLs de producao e ajustes finais de portfolio serao adicionados depois do deploy.

## Stack

- Frontend: Next.js, React, TypeScript, Tailwind CSS
- Backend: Java 21, Spring Boot, Spring Web, Spring Security, Spring Data JPA
- Banco de dados: PostgreSQL
- Migracoes: Flyway
- Autenticacao: JWT com senhas criptografadas usando BCrypt
- CI: GitHub Actions
- Deploy planejado: Vercel no frontend, Render no backend e Supabase PostgreSQL

## Estrutura Do Projeto

```text
linkito/
  backend/      API Spring Boot
  frontend/     Aplicacao Next.js
  docker-compose.yml
```

## Funcionalidades Principais

- Cadastro e login de usuarios
- Painel protegido por JWT
- Criacao, listagem, atualizacao e remocao de links encurtados
- Rota publica de redirecionamento usando codigo curto
- Registro de cliques com IP e user agent
- Dashboard e estatisticas por link
- Filtro simples de limite de requisicoes
- Versionamento de banco com Flyway

## Arquitetura De Producao

```text
Navegador
  -> Vercel / Next.js
  -> Proxy de API do Next.js
  -> Render / API Spring Boot
  -> Supabase / PostgreSQL
```

O frontend usa um proxy de API do Next.js para que as requisicoes do navegador passem pelo dominio do frontend, enquanto a rota server-side encaminha as chamadas para o backend usando `BACKEND_URL`.

A exibicao dos links encurtados e controlada por `NEXT_PUBLIC_PUBLIC_URL`. Em desenvolvimento local, ela aponta para `http://localhost:8080`; em producao, deve apontar para a URL publica do backend ou para um dominio customizado de redirecionamento.

## Variaveis De Ambiente

Backend:

```env
SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/linkito
SPRING_DATASOURCE_USERNAME=postgres
SPRING_DATASOURCE_PASSWORD=postgres
JWT_SEGREDO=local-dev-secret-change-me-change-me-change-me-123456
JWT_EXPIRACAO=3600000
PORT=8080
```

Frontend:

```env
BACKEND_URL=http://localhost:8080
NEXT_PUBLIC_PUBLIC_URL=http://localhost:8080
```

Veja [backend/.env.example](backend/.env.example) e [frontend/.env.example](frontend/.env.example).

## Rodando Localmente

Suba o PostgreSQL e o backend com Docker:

```bash
docker compose up --build
```

Rode o frontend:

```bash
cd frontend
npm install
npm run dev
```

Frontend: `http://localhost:3000`

Backend: `http://localhost:8080`

Health check: `http://localhost:8080/actuator/health`

## Comandos Do Backend

```bash
cd backend
./mvnw test
./mvnw spring-boot:run
```

No Windows:

```powershell
cd backend
.\mvnw.cmd test
.\mvnw.cmd spring-boot:run
```

## Comandos Do Frontend

```bash
cd frontend
npm install
npm run dev
npm run build
```

## Rotas Principais Da API

Autenticacao:

- `POST /api/auth/register`
- `POST /api/auth/login`
- `GET /api/auth/me`

Links:

- `POST /api/links`
- `GET /api/links`
- `GET /api/links/{id}`
- `PUT /api/links/{id}`
- `DELETE /api/links/{id}`

Redirecionamento:

- `GET /r/{codigoCurto}`

Estatisticas:

- `GET /api/stats/dashboard`
- `GET /api/stats/links/{id}`

## CI

O GitHub Actions valida:

- Testes do backend com Maven e Java 21
- Build de producao do frontend com Node.js

O deploy sera configurado depois que o projeto for publicado no GitHub e conectado a Vercel, Render e Supabase.
