# 🔗 Linkito

![GitHub language count](https://img.shields.io/github/languages/count/vavito/Linkito?style=for-the-badge)
![Status](https://img.shields.io/badge/Status-Em%20evolu%C3%A7%C3%A3o-yellow?style=for-the-badge)
![Deploy](https://img.shields.io/badge/Deploy-Vercel%20%2B%20Render%20%2B%20Supabase-black?style=for-the-badge)

Aplicação web fullstack de encurtamento de links desenvolvida com **Spring Boot** no backend e **Next.js + React + TypeScript** no frontend.

O projeto permite criar links encurtados, gerenciar links criados, pausar/redirecionar URLs, registrar cliques e visualizar estatísticas em um painel autenticado com **JWT**.

---

## Demonstração online

- **Frontend:** [https://linkito-ten.vercel.app](https://linkito-ten.vercel.app)
- **Backend:** [https://linkito-backend-v1e8.onrender.com](https://linkito-backend-v1e8.onrender.com)
- **Health check:** [https://linkito-backend-v1e8.onrender.com/actuator/health](https://linkito-backend-v1e8.onrender.com/actuator/health)

> O backend está hospedado no plano gratuito do Render. Após um período sem tráfego, o serviço pode dormir e a primeira requisição pode demorar alguns segundos.

---

## Imagens e vídeo demonstrativo

![Tela de login](./docs/assets/login.png)
_Tela inicial de login e cadastro._

![Home](./docs/assets/home.png)
_Visão geral do painel com métricas principais._

![Criar link](./docs/assets/criar-link.png)
_Módulo para criar um novo link encurtado._

![Links criados](./docs/assets/links-criados.png)
_Biblioteca de links com busca, ordenação, status e ações._

![Análises](./docs/assets/analises.png)
_Módulo de estatísticas com ranking e cliques recentes._

![Conta](./docs/assets/conta.png)
_Módulo de conta para editar perfil e senha._

![Criando um link](./docs/assets/criacao-link.gif)
_Fluxo criando um link, copiando a URL encurtada e visualizando os dados no painel._

---

## Objetivo

Este projeto foi desenvolvido para praticar a construção e publicação de uma aplicação **full stack** com backend Java, frontend moderno, banco relacional em produção e fluxo de CI/CD.

O foco principal foi trabalhar com:

- Autenticação com JWT
- API REST com Spring Boot
- Persistência com PostgreSQL
- Migrations com Flyway
- Deploy separado de frontend, backend e banco de dados
- Integração entre Next.js e Spring Boot via proxy server-side
- Tratamento de erros no backend e frontend
- Estatísticas e registro de eventos de acesso
- Fluxo de GitHub com branches, Pull Requests e GitHub Actions

---

## Funcionalidades

- **Criar conta e fazer login**
- **Autenticar requisições com JWT**
- **Criar links encurtados**
- **Adicionar título opcional ao link**
- **Listar links criados**
- **Buscar links por título, destino ou código**
- **Ordenar links por recentes, antigos, mais acessados e menos acessados**
- **Copiar URL encurtada**
- **Pausar e reativar links**
- **Excluir links com modal de confirmação**
- **Redirecionar usuários pela rota pública `/r/{codigoCurto}`**
- **Registrar cliques nos links**
- **Exibir estatísticas gerais no dashboard**
- **Exibir estatísticas por link**
- **Atualizar dados manualmente**
- **Auto-refresh de dados voláteis**
- **Editar nome, email e senha da conta**
- **Exibir mensagens amigáveis para erros esperados**

---

## Tecnologias utilizadas

### Backend

- **Java 21**
- **Spring Boot 3**
- **Spring Web MVC**
- **Spring Security**
- **Spring Data JPA**
- **Spring Validation**
- **PostgreSQL**
- **Flyway**
- **JWT com JJWT**
- **BCrypt**
- **Maven**
- **JUnit**
- **Mockito**
- **Spring Actuator**
- **Docker**

### Frontend

- **Next.js**
- **React**
- **TypeScript**
- **Tailwind CSS**
- **Framer Motion**
- **Lucide React**
- **Vercel**

### Infraestrutura e CI/CD

- **GitHub**
- **GitHub Actions**
- **Vercel** para frontend
- **Render** para backend
- **Supabase PostgreSQL** para banco de dados

---

## Arquitetura

O projeto foi dividido em duas aplicações principais:

- `backend` → API REST responsável por autenticação, gerenciamento de usuários, links, redirecionamento, registro de cliques e estatísticas.
- `frontend` → Interface web responsável por autenticação, dashboard, criação e gerenciamento de links, visualização de estatísticas e conta do usuário.

Em produção, a arquitetura segue o fluxo:

```text
Usuário
  -> Vercel / Next.js
  -> Proxy de API do Next.js
  -> Render / Spring Boot
  -> Supabase / PostgreSQL
```

O frontend não chama diretamente a API do navegador. As chamadas passam por uma rota proxy do Next.js em `frontend/app/api/[...path]/route.ts`, que encaminha as requisições para o backend usando a variável `BACKEND_URL`.

Isso reduz problemas de CORS e centraliza a comunicação com o backend no lado server-side do Next.js.

---

## Fluxo de encurtamento

O fluxo principal da aplicação segue o padrão:

**Login → Criar link → Gerar código curto → Compartilhar URL → Redirecionar → Registrar clique → Atualizar estatísticas**

1. O usuário cria uma conta ou faz login.
2. O frontend armazena o token JWT no navegador.
3. O usuário informa uma URL original e, opcionalmente, um título.
4. O backend valida a URL e gera um código curto único.
5. O link fica disponível pela rota pública `/r/{codigoCurto}`.
6. Quando alguém acessa o link curto, o backend busca a URL original.
7. O clique é registrado com data, IP e user agent.
8. O usuário acompanha os totais no dashboard e na tela de análises.

---

## Estatísticas e rastreamento

O Linkito registra cada acesso feito por um link encurtado.

Para cada clique, o backend salva:

- ID do link encurtado
- Endereço IP recebido na requisição
- User agent do navegador/dispositivo
- Data e hora do clique

Esses dados são usados para alimentar:

- total de cliques por link
- link mais acessado
- cliques recentes
- ranking de links
- métricas do dashboard

Atualmente, o user agent é armazenado em formato bruto. Uma melhoria futura possível é transformar esse valor em informações mais amigáveis, como navegador, sistema operacional e tipo de dispositivo.

---

## Tratamento de erros

O projeto trata erros esperados no backend e no frontend para evitar mensagens técnicas para o usuário.

Exemplos de erros tratados:

- Email inválido
- Email já cadastrado
- Senha inválida
- URL original inválida
- Token ausente ou inválido
- Backend indisponível

No backend, erros de validação são retornados em formato previsível:

```json
{
  "mensagem": "Informe uma URL válida com http:// ou https://.",
  "campos": {
    "urlOriginal": "Informe uma URL válida com http:// ou https://."
  }
}
```

No frontend, essas mensagens são normalizadas e exibidas dentro da interface.

---

## Deploy

O deploy foi feito separando responsabilidades:

- **Frontend:** Vercel
- **Backend:** Render
- **Banco de dados:** Supabase PostgreSQL

### Variáveis de ambiente do backend

```env
SPRING_DATASOURCE_URL=jdbc:postgresql://HOST:PORT/postgres?sslmode=require
SPRING_DATASOURCE_USERNAME=postgres.xxxxx
SPRING_DATASOURCE_PASSWORD=sua_senha_do_banco
JWT_SEGREDO=seu_segredo_jwt
JWT_EXPIRACAO=3600000
```

### Variáveis de ambiente do frontend

```env
BACKEND_URL=https://linkito-backend-v1e8.onrender.com
NEXT_PUBLIC_PUBLIC_URL=https://linkito-backend-v1e8.onrender.com
```

`BACKEND_URL` é usada pelo proxy server-side do Next.js.

`NEXT_PUBLIC_PUBLIC_URL` é usada para montar a URL pública do link encurtado.

---

## CI/CD

O projeto usa **GitHub Actions** para validar Pull Requests e pushes.

### Backend CI

Executa:

```bash
./mvnw test
```

Valida testes automatizados do backend com Java 21 e Maven.

### Frontend CI

Executa:

```bash
npm ci
npm run build
```

Valida instalação limpa de dependências e build de produção do Next.js.

O deploy automático é feito pelas integrações do GitHub com Vercel e Render após merge na branch `main`.

---

## Como rodar

### Banco e backend com Docker

Na raiz do projeto:

```bash
docker compose up --build
```

Backend:

```text
http://localhost:8080
```

Health check:

```text
http://localhost:8080/actuator/health
```

### Backend local

```bash
cd backend
./mvnw spring-boot:run
```

No Windows:

```powershell
cd backend
.\mvnw.cmd spring-boot:run
```

### Frontend local

```bash
cd frontend
npm install
npm run dev
```

Frontend:

```text
http://localhost:3000
```

---

## Estrutura do projeto

```text
linkito/
  backend/
    src/main/java/com/linkito/
      autenticacao/
      clique/
      dto/
      estatisticas/
      limite/
      link/
      redirecionamento/
      seguranca/
      servico/
      usuario/
    src/main/resources/db/migration/
    src/test/
    Dockerfile
    pom.xml

  frontend/
    app/
      api/[...path]/
      globals.css
      layout.tsx
      page.tsx
    package.json

  .github/workflows/
  docker-compose.yml
  DEPLOY.md
```

---

## Melhorias futuras

- [ ] Adicionar domínio próprio para frontend e links encurtados.
- [ ] Exibir navegador, sistema operacional e dispositivo a partir do user agent.
- [ ] Criar gráficos mais detalhados de cliques por período.
- [ ] Adicionar filtro por data nas estatísticas.
- [ ] Implementar recuperação de senha por email.
- [ ] Adicionar testes de integração para controllers.
- [ ] Melhorar acessibilidade dos modais e formulários.
- [ ] Avaliar plano pago ou alternativa sem cold start para o backend.

---

## Autor

Desenvolvido por **João Victor**.

- [LinkedIn](https://www.linkedin.com/in/joao-victor-moreira-almeida/)
- [GitHub](https://github.com/vavito)
