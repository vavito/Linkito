# Linkito

Aplicação full stack para criar, organizar e acompanhar links curtos. Desenvolvida como projeto de portfólio com API própria, autenticação e implantação em containers na AWS.

**Demonstração:** [https://100.58.229.18](https://100.58.229.18/)

## Funcionalidades

- Cadastro e login com autenticação JWT.
- Criação, listagem, busca, pausa, reativação e exclusão de links.
- Redirecionamento público e registro de cliques.
- Painel com métricas gerais e estatísticas por link.
- Gerenciamento de perfil.

## Tecnologias

- **Frontend:** Next.js, React, TypeScript e Tailwind CSS.
- **Backend:** Java 21, Spring Boot, Spring Security, JPA e Flyway.
- **Banco:** PostgreSQL.
- **Infraestrutura:** AWS EC2, RDS, ECR, Systems Manager, Nginx e GitHub Actions.
- **Containers:** Docker e Docker Compose.

## Arquitetura em produção

```text
Navegador --HTTPS--> Nginx na EC2
                         ├── Frontend Next.js (container)
                         └── API Spring Boot (container) --> RDS PostgreSQL privado

GitHub Actions --OIDC--> ECR --SSM--> implantação dos containers na EC2
```

O Nginx termina HTTPS e encaminha as requisições. O frontend usa um proxy server-side para acessar a API. As portas dos containers da aplicação ficam restritas à própria instância; o banco permanece em sub-rede privada.

## Docker

Na raiz do repositório, o Compose inicia o PostgreSQL e a API. O volume nomeado `postgres_data` mantém os dados entre reinicializações:

```bash
docker compose up --build -d
```

- API: `http://localhost:8080`
- Health check: `http://localhost:8080/actuator/health`

Para iniciar o frontend localmente, em outro terminal:

```bash
cd frontend
npm ci
npm run dev
```

Frontend: `http://localhost:3000`. A rota `/api` encaminha as chamadas para a API local em `http://localhost:8080` por padrão.

As imagens de produção são construídas em etapas com Dockerfiles próprios. O GitHub Actions valida backend e frontend, publica as imagens no Amazon ECR e usa OIDC e Systems Manager para implantá-las na EC2.

## Repositório

[github.com/vavito/Linkito](https://github.com/vavito/Linkito)
