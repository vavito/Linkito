# Guia De Deploy Do Linkito

Este e um checklist curto para a configuracao inicial de producao.

## 1. Supabase

Crie um projeto no Supabase e copie os dados de conexao do PostgreSQL.

O backend vai usar estes valores:

```env
SPRING_DATASOURCE_URL=jdbc:postgresql://HOST:PORT/postgres?sslmode=require
SPRING_DATASOURCE_USERNAME=postgres
SPRING_DATASOURCE_PASSWORD=sua_senha_do_banco
```

O Flyway roda automaticamente quando o backend inicia.

## 2. Render Backend

Crie um Web Service no Render para a pasta `backend`.

Configuracao recomendada:

```text
Runtime: Docker
Root directory: backend
Health check path: /actuator/health
```

Variaveis de ambiente:

```env
SPRING_DATASOURCE_URL=jdbc:postgresql://HOST:PORT/postgres?sslmode=require
SPRING_DATASOURCE_USERNAME=postgres
SPRING_DATASOURCE_PASSWORD=sua_senha_do_banco
JWT_SEGREDO=use_um_segredo_longo_e_aleatorio
JWT_EXPIRACAO=3600000
```

O Render fornece `PORT` automaticamente. O backend le esse valor por meio de `server.port=${PORT:8080}`.

## 3. Vercel Frontend

Crie um projeto na Vercel para a pasta `frontend`.

Variaveis de ambiente:

```env
BACKEND_URL=https://seu-servico-render.onrender.com
NEXT_PUBLIC_PUBLIC_URL=https://seu-servico-render.onrender.com
```

`BACKEND_URL` e usada pelo proxy de API do Next.js.

`NEXT_PUBLIC_PUBLIC_URL` e usada para exibir/copiar a URL publica encurtada.

## 4. GitHub Actions

O repositorio inclui workflows de CI para:

- Testes do backend
- Build de producao do frontend

A Vercel e o Render podem cuidar dos deploys automaticos diretamente a partir do repositorio GitHub depois que o projeto for conectado.

## 5. Ajustes Finais De Portfolio

Depois que a producao estiver online:

- Adicionar URLs de producao ao `README.md`
- Adicionar screenshots e GIFs
- Adicionar imagem ou diagrama de arquitetura
- Adicionar badges de status do CI
- Adicionar uma explicacao curta das decisoes de deploy
