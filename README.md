# ReInveste API - Sistema de Auxílio contra Vício em Apostas 

Sistema de auxílio para pessoas com vício em apostas, focado em incentivar investimentos como alternativa saudável. Desenvolvido em Spring Boot com arquitetura limpa, princípios SOLID e segurança robusta.

![Java](https://img.shields.io/badge/Java-21-orange)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5.4-brightgreen)
![MySQL](https://img.shields.io/badge/MySQL-8.0-blue)
![License](https://img.shields.io/badge/License-MIT-yellow)

---

## 👥 Integrantes da Equipe

| Nome | RM |
|------|-----|
| Rodrigo Fernandes Serafim | RM550816 |
| João Antonio Rihan | RM99656 |
| Adriano Lopes | RM98574 |
| Henrique de Brito | RM98831 |
| Rodrigo Lima | RM98326 |

**Workspace Postman:** https://warped-resonance-873879.postman.co/workspace/safsa~7f4fc7d5-2d61-44c8-9818-fc1b0061248b/collection/29513449-b238ceaa-a682-4943-8559-85813c027049?action=share&source=copy-link&creator=29513449

---

##  Descrição do Projeto

O **ReInveste API** é uma aplicação REST robusta que auxilia pessoas com vício em apostas a redirecionarem seus recursos para investimentos saudáveis. O sistema oferece:

-  **Autenticação segura** com JWT (JSON Web Token)
-  **Gestão completa de usuários** com controle de vício
-  **Metas personalizadas** de investimento e economia
-  **Rastreamento de transações** financeiras
-  **Estatísticas e relatórios** detalhados
-  **Documentação interativa** com Swagger/OpenAPI
-  **Cobertura de testes** unitários e de integração

### Diagrama de Classes
![Diagrama](img/reinveste.png)

---

##  Arquitetura e Princípios SOLID

O projeto foi desenvolvido seguindo os princípios **SOLID** e boas práticas de desenvolvimento:

-  **Single Responsibility Principle (SRP)**: Cada classe tem uma única responsabilidade
-  **Open/Closed Principle (OCP)**: Uso de interfaces para extensibilidade
-  **Liskov Substitution Principle (LSP)**: Implementações respeitam contratos
-  **Interface Segregation Principle (ISP)**: Interfaces específicas por contexto
-  **Dependency Inversion Principle (DIP)**: Controllers dependem de interfaces, não implementações

### Camadas da Aplicação
```
┌─────────────────────────────────────────────┐
│           Controllers (REST API)            │
│  - AuthController                           │
│  - UsuarioController                        │
│  - MetaController                           │
│  - TransacaoController                      │
└─────────────────┬───────────────────────────┘
                  │ (Interfaces)
┌─────────────────▼───────────────────────────┐
│              Services Layer                 │
│  - IUsuarioService → UsuarioService         │
│  - IMetaService → MetaService               │
│  - ITransacaoService → TransacaoService     │
│  - IValidationService → ValidationService   │
└─────────────────┬───────────────────────────┘
                  │
┌─────────────────▼───────────────────────────┐
│           Repository Layer (JPA)            │
│  - UsuarioRepository                        │
│  - MetaRepository                           │
│  - TransacaoRepository                      │
└─────────────────┬───────────────────────────┘
                  │
┌─────────────────▼───────────────────────────┐
│              Database (MySQL)               │
└─────────────────────────────────────────────┘
```

---

##  Tecnologias Utilizadas

### Core
- **Java 21** - Linguagem de programação
- **Spring Boot 3.5.4** - Framework principal
- **Maven** - Gerenciamento de dependências

### Persistência
- **Spring Data JPA** - Abstração para persistência
- **MySQL 8.0** - Banco de dados relacional
- **Flyway** - Controle de versão do banco de dados

### Segurança
- **Spring Security** - Framework de segurança
- **JWT (JSON Web Token)** - Autenticação stateless
- **BCrypt** - Criptografia de senhas

### Documentação
- **SpringDoc OpenAPI 3** - Documentação interativa da API
- **Swagger UI** - Interface visual para testes

### Validação e Utilidades
- **Bean Validation** - Validação de dados
- **Lombok** - Redução de código boilerplate

### Testes
- **JUnit 5** - Framework de testes
- **Mockito** - Mocks para testes unitários
- **Spring Boot Test** - Testes de integração
- **H2 Database** - Banco em memória para testes

---

## 🚀 Como Executar a Aplicação

### 1. Pré-requisitos

Certifique-se de ter instalado:
- ☕ **Java 21** ou superior ([Download](https://www.oracle.com/java/technologies/downloads/))
- 🗄️ **MySQL 8.0** ou superior ([Download](https://dev.mysql.com/downloads/))
- 📦 **Maven 3.6** ou superior ([Download](https://maven.apache.org/download.cgi))

### 2. Clone o Repositório

```bash
git clone https://github.com/joaorihan/ReInveste-API.git
cd ReInveste-API
```

### 3. Configure o Banco de Dados

#### 3.1. Inicie o MySQL
Certifique-se de que o MySQL está rodando:
```bash
# Windows 
net start MySQL80
```

#### 3.2. Crie o banco de dados
```sql
CREATE DATABASE xp_investimento_auxilio;
```

#### 3.3. Configure as credenciais
Edite o arquivo `src/main/resources/application.properties`:

```properties
spring.datasource.url=jdbc:mysql://localhost/xp_investimento_auxilio
spring.datasource.username=seu_usuario_mysql
spring.datasource.password=sua_senha_mysql
```

### 4. Execute a Aplicação

#### Usando Maven
```bash
mvn spring-boot:run
```

### 5. Testando

### 5. Acesse a Aplicação

A aplicação estará disponível em:
- **API Base URL**: http://localhost:8080
- **Swagger UI**: http://localhost:8080/swagger-ui.html
- **OpenAPI Docs**: http://localhost:8080/v3/api-docs

---

## 🧪 Como Rodar os Testes

O projeto possui **33 testes** entre unitários e de integração.

### Executar Todos os Testes
```bash
mvn test
```

### Executar Apenas Testes Unitários
```bash
mvn test -Dtest=*ServiceTest
```

### Executar Apenas Testes de Integração
```bash
mvn test -Dtest=*ControllerTest
```

### Gerar Relatório de Cobertura
```bash
mvn clean test jacoco:report
```

### Estrutura dos Testes

#### Testes Unitários (Mockito)
- ✅ **UsuarioServiceTest** - 10 testes
- ✅ **MetaServiceTest** - 9 testes
- ✅ **TransacaoServiceTest** - 10 testes

#### Testes de Integração (MockMvc)
- ✅ **AuthControllerTest** - 6 testes
- ✅ **UsuarioControllerTest** - 6 testes
- ✅ **MetaControllerTest** - 5 testes
- ✅ **TransacaoControllerTest** - 6 testes

---

## 🔐 Autenticação e Segurança

A API utiliza **autenticação JWT (JSON Web Token)** com segurança stateless.

### 1. Registrar um Usuário

**Endpoint:** `POST /auth/register` (público)

```bash
curl -X POST http://localhost:8080/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "nome": "João Silva",
    "email": "joao@email.com",
    "senha": "senha123",
    "telefone": "11987654321",
    "cpf": "12345678901",
    "dataNascimento": "1990-01-15",
    "nivelVicio": "MODERADO",
    "valorGastoApostas": 1500.00,
    "metaInvestimentoMensal": 800.00,
    "dataUltimaAposta": "2024-01-10T14:30:00",
    "endereco": {
      "logradouro": "Rua das Flores",
      "numero": "123",
      "complemento": "Apto 45",
      "bairro": "Centro",
      "cidade": "São Paulo",
      "uf": "SP",
      "cep": "01310100"
    }
  }'
```

### 2. Fazer Login

**Endpoint:** `POST /auth/login` (público)

```bash
curl -X POST http://localhost:8080/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "joao@email.com",
    "senha": "senha123"
  }'
```

**Resposta:**
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "tipo": "Bearer",
  "usuarioId": 1,
  "nome": "João Silva",
  "email": "joao@email.com"
}
```

### 3. Usar o Token em Requisições Protegidas

Adicione o header `Authorization` com o token:

```bash
curl -X GET http://localhost:8080/usuarios \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
```

### Endpoints Públicos (sem autenticação)
- `POST /auth/login`
- `POST /auth/register`
- `GET /health`
- `/swagger-ui/**`
- `/v3/api-docs/**`

### Endpoints Protegidos (requerem JWT)
- Todos os outros endpoints de `/usuarios`, `/metas` e `/transacoes`

---

## 📚 Documentação da API (Swagger)

A documentação interativa está disponível através do **Swagger UI**:

🔗 **http://localhost:8080/swagger-ui.html**

### Funcionalidades do Swagger:
- 📖 Documentação completa de todos os endpoints
- 🧪 Teste interativo de endpoints
- 🔐 Suporte para autenticação JWT
- 📝 Exemplos de requisições e respostas
- 🏷️ Organização por tags (Autenticação, Usuários, Metas, Transações)

### Como Usar o Swagger com JWT:
1. Acesse o Swagger UI
2. Faça login através do endpoint `/auth/login`
3. Copie o token retornado
4. Clique no botão **"Authorize"** (cadeado) no topo da página
5. Cole o token no campo `bearerAuth` (sem o prefixo "Bearer")
6. Clique em **"Authorize"**
7. Agora você pode testar todos os endpoints protegidos!

---

## 📋 Endpoints da API

### 🔐 Autenticação

| Método | Endpoint | Descrição | Autenticação |
|--------|----------|-----------|--------------|
| `POST` | `/auth/login` | Realizar login | ❌ Pública |
| `POST` | `/auth/register` | Registrar novo usuário | ❌ Pública |

### 👤 Usuários

| Método | Endpoint | Descrição | Autenticação |
|--------|----------|-----------|--------------|
| `GET` | `/usuarios` | Listar usuários (paginado) | ✅ JWT |
| `POST` | `/usuarios` | Cadastrar usuário | ✅ JWT |
| `GET` | `/usuarios/{id}` | Buscar usuário por ID | ✅ JWT |
| `PUT` | `/usuarios` | Atualizar usuário | ✅ JWT |
| `DELETE` | `/usuarios/{id}` | Excluir usuário (lógico) | ✅ JWT |
| `POST` | `/usuarios/{id}/aposta` | Registrar aposta | ✅ JWT |
| `PUT` | `/usuarios/{id}/dias-sem-apostar` | Atualizar dias sem apostar | ✅ JWT |
| `GET` | `/usuarios/meta-alcancada` | Usuários com meta alcançada | ✅ JWT |
| `GET` | `/usuarios/nivel-vicio/{nivel}` | Buscar por nível de vício | ✅ JWT |
| `GET` | `/usuarios/estatisticas/sem-apostar` | Contar usuários sem apostar | ✅ JWT |
| `GET` | `/usuarios/estatisticas/media-gasto-apostas` | Média de gastos | ✅ JWT |

### 🎯 Metas

| Método | Endpoint | Descrição | Autenticação |
|--------|----------|-----------|--------------|
| `POST` | `/metas/usuario/{usuarioId}` | Criar meta para usuário | ✅ JWT |
| `GET` | `/metas/usuario/{usuarioId}` | Listar metas do usuário | ✅ JWT |
| `GET` | `/metas/usuario/{usuarioId}/ativas` | Metas ativas do usuário | ✅ JWT |
| `GET` | `/metas/{id}` | Buscar meta por ID | ✅ JWT |
| `PUT` | `/metas/{id}/progresso` | Atualizar progresso da meta | ✅ JWT |
| `DELETE` | `/metas/{id}` | Cancelar meta | ✅ JWT |
| `GET` | `/metas/vencidas` | Listar metas vencidas | ✅ JWT |
| `GET` | `/metas/usuario/{usuarioId}/concluidas/count` | Contar metas concluídas | ✅ JWT |

### 💰 Transações

| Método | Endpoint | Descrição | Autenticação |
|--------|----------|-----------|--------------|
| `POST` | `/transacoes/usuario/{usuarioId}` | Registrar transação | ✅ JWT |
| `GET` | `/transacoes/usuario/{usuarioId}` | Listar transações do usuário | ✅ JWT |
| `GET` | `/transacoes/usuario/{usuarioId}/tipo/{tipo}` | Transações por tipo | ✅ JWT |
| `GET` | `/transacoes/usuario/{usuarioId}/periodo` | Transações por período | ✅ JWT |
| `PUT` | `/transacoes/{id}/confirmar` | Confirmar transação | ✅ JWT |
| `PUT` | `/transacoes/{id}/cancelar` | Cancelar transação | ✅ JWT |
| `GET` | `/transacoes/usuario/{usuarioId}/estatisticas/investimentos` | Total investido | ✅ JWT |
| `GET` | `/transacoes/usuario/{usuarioId}/estatisticas/economias` | Total economizado | ✅ JWT |
| `GET` | `/transacoes/usuario/{usuarioId}/estatisticas/apostas` | Total em apostas | ✅ JWT |
| `GET` | `/transacoes/usuario/{usuarioId}/estatisticas/apostas-recentes` | Apostas recentes | ✅ JWT |

### 🏥 Health Check

| Método | Endpoint | Descrição | Autenticação |
|--------|----------|-----------|--------------|
| `GET` | `/health` | Status da aplicação | ❌ Pública |

---

## ✨ Funcionalidades Principais

### 👤 Gestão de Usuários
- ✅ Cadastro completo com dados pessoais e nível de vício
- ✅ Autenticação segura com JWT
- ✅ Senhas criptografadas com BCrypt
- ✅ Controle de dias sem apostar
- ✅ Monitoramento de gastos com apostas
- ✅ Definição de metas de investimento mensal
- ✅ Exclusão lógica (soft delete)

### 🎯 Gestão de Metas
- ✅ Criação de metas personalizadas (investimento, economia)
- ✅ Acompanhamento do progresso em tempo real
- ✅ Cálculo automático de percentual de conclusão
- ✅ Notificações de metas vencidas
- ✅ Limite de 5 metas ativas por usuário
- ✅ Histórico completo de metas

### 💰 Gestão de Transações
- ✅ Registro de investimentos, economias e apostas
- ✅ Associação de transações com metas
- ✅ Validações de negócio para evitar apostas excessivas
- ✅ Relatórios por período e tipo
- ✅ Confirmação/cancelamento de transações
- ✅ Atualização automática de progresso de metas

### 📊 Relatórios e Estatísticas
- ✅ Total de investimentos realizados
- ✅ Valor economizado vs gasto em apostas
- ✅ Dias consecutivos sem apostar
- ✅ Média de gastos com apostas por usuário
- ✅ Contagem de usuários que atingiram metas
- ✅ Apostas recentes por período

---

## 📊 Enumerações (Enums)

### Níveis de Vício
```java
- LEVE       // Aposta ocasionalmente
- MODERADO   // Aposta regularmente  
- GRAVE      // Aposta frequentemente
- CRITICO    // Aposta diariamente com problemas graves
```

### Tipos de Meta
```java
- INVESTIMENTO  // Meta de investimento financeiro
- ECONOMIA      // Meta de economia/poupança
```

### Tipos de Transação
```java
- INVESTIMENTO  // Aplicação em ativos financeiros
- ECONOMIA      // Valor economizado
- APOSTA        // Gasto com apostas (controle)
```

### Status de Meta
```java
- ATIVA       // Meta em andamento
- CONCLUIDA   // Meta atingida
- CANCELADA   // Meta cancelada pelo usuário
```

### Status de Transação
```java
- PENDENTE     // Aguardando confirmação
- CONFIRMADA   // Transação confirmada
- CANCELADA    // Transação cancelada
```

---

## 🔧 Configurações Importantes

### Banco de Dados
```properties
# Configuração de produção
spring.jpa.hibernate.ddl-auto=validate
spring.flyway.enabled=true

# As migrações estão em: src/main/resources/db/migration/
# V1 - Criar tabela usuarios
# V2 - Criar tabela metas
# V3 - Criar tabela transacoes
# V4 - Criar índices
# V5 - Adicionar colunas faltantes em usuarios
# V6 - Adicionar coluna senha em usuarios
```

### JWT
```properties
# Token expira em 24 horas (86400000 ms)
jwt.expiration=86400000
# Chave secreta (mínimo 256 bits)
jwt.secret=sua-chave-secreta-aqui
```

### Paginação Padrão
- **Tamanho**: 10 registros por página
- **Ordenação**: 
  - Usuários: por nome (ASC)
  - Metas: por data de criação (DESC)
  - Transações: por data da transação (DESC)

---

## 🧪 Regras de Validação

### Usuários
- ✅ Email único e formato válido
- ✅ CPF único com 11 dígitos
- ✅ Senha mínimo 6 caracteres
- ✅ Telefone com 10 ou 11 dígitos
- ✅ Data de nascimento no passado
- ✅ Nível de vício obrigatório

### Metas
- ✅ Máximo 5 metas ativas por usuário
- ✅ Data de fim posterior à data de início
- ✅ Data de início no futuro
- ✅ Valor alvo maior que zero
- ✅ Descrição obrigatória

### Transações
- ✅ Valor maior que zero
- ✅ Investimentos devem estar associados a uma meta
- ✅ Validação de apostas excessivas (máximo 2x a meta de investimento)
- ✅ Tipo de transação obrigatório

---

## 🏗️ Estrutura do Projeto

```
ReInveste-API/
├── src/
│   ├── main/
│   │   ├── java/.../spring_boot_project/
│   │   │   ├── config/                    # Configurações
│   │   │   │   └── OpenApiConfig.java    # Config do Swagger
│   │   │   ├── controller/                # Controllers REST
│   │   │   │   ├── AuthController.java   # Autenticação
│   │   │   │   ├── UsuarioController.java
│   │   │   │   ├── MetaController.java
│   │   │   │   ├── TransacaoController.java
│   │   │   │   └── HealthCheckController.java
│   │   │   ├── service/                   # Lógica de negócio
│   │   │   │   ├── IUsuarioService.java  # Interface
│   │   │   │   ├── UsuarioService.java   # Implementação
│   │   │   │   ├── IMetaService.java
│   │   │   │   ├── MetaService.java
│   │   │   │   ├── ITransacaoService.java
│   │   │   │   ├── TransacaoService.java
│   │   │   │   └── validation/
│   │   │   │       ├── IValidationService.java
│   │   │   │       └── ValidationService.java
│   │   │   ├── security/                  # Segurança e JWT
│   │   │   │   ├── SecurityConfig.java
│   │   │   │   ├── JwtUtil.java
│   │   │   │   ├── SecurityFilter.java
│   │   │   │   └── UserDetailsServiceImpl.java
│   │   │   ├── usuario/                   # Domínio de Usuário
│   │   │   │   ├── Usuario.java
│   │   │   │   ├── UsuarioRepository.java
│   │   │   │   ├── DadosCadastroUsuario.java
│   │   │   │   ├── DadosListagemUsuario.java
│   │   │   │   ├── DadosAtualizacaoUsuario.java
│   │   │   │   ├── DadosLogin.java
│   │   │   │   ├── DadosTokenJWT.java
│   │   │   │   └── NivelVicio.java
│   │   │   ├── meta/                      # Domínio de Meta
│   │   │   │   ├── Meta.java
│   │   │   │   ├── MetaRepository.java
│   │   │   │   ├── DadosCadastroMeta.java
│   │   │   │   ├── DadosListagemMeta.java
│   │   │   │   ├── TipoMeta.java
│   │   │   │   └── StatusMeta.java
│   │   │   ├── transacao/                 # Domínio de Transação
│   │   │   │   ├── Transacao.java
│   │   │   │   ├── TransacaoRepository.java
│   │   │   │   ├── DadosCadastroTransacao.java
│   │   │   │   ├── DadosListagemTransacao.java
│   │   │   │   ├── TipoTransacao.java
│   │   │   │   └── StatusTransacao.java
│   │   │   ├── endereco/                  # Value Object
│   │   │   │   ├── Endereco.java
│   │   │   │   └── DadosEndereco.java
│   │   │   ├── exception/                 # Tratamento de erros
│   │   │   │   ├── TratadorDeErros.java
│   │   │   │   ├── ResourceNotFoundException.java
│   │   │   │   ├── BusinessRuleException.java
│   │   │   │   └── DuplicateResourceException.java
│   │   │   └── SpringBootProject3EspgApplication.java
│   │   └── resources/
│   │       ├── application.properties     # Configurações
│   │       └── db/migration/             # Migrações Flyway
│   │           ├── V1__create-table-usuarios.sql
│   │           ├── V2__create-table-metas.sql
│   │           ├── V3__create-table-transacoes.sql
│   │           ├── V4__create-indexes.sql
│   │           ├── V5__add-missing-columns-usuarios.sql
│   │           └── V6__add-senha-column-usuarios.sql
│   └── test/
│       ├── java/.../spring_boot_project/
│       │   ├── service/                   # Testes Unitários
│       │   │   ├── UsuarioServiceTest.java
│       │   │   ├── MetaServiceTest.java
│       │   │   └── TransacaoServiceTest.java
│       │   └── controller/                # Testes de Integração
│       │       ├── AuthControllerTest.java
│       │       ├── UsuarioControllerTest.java
│       │       ├── MetaControllerTest.java
│       │       └── TransacaoControllerTest.java
│       └── resources/
│           └── application-test.properties # Config de testes
├── img/
│   └── reinveste.png                      # Diagrama de classes
├── pom.xml                                # Dependências Maven
└── README.md                              # Este arquivo
```

---

## 🚨 Tratamento de Erros

A API retorna erros padronizados com os seguintes status:

| Status | Descrição | Exemplo |
|--------|-----------|---------|
| `400` | Bad Request | Dados de entrada inválidos |
| `401` | Unauthorized | Token JWT inválido/expirado |
| `403` | Forbidden | Sem token JWT |
| `404` | Not Found | Recurso não encontrado |
| `409` | Conflict | Email/CPF duplicado |
| `500` | Internal Server Error | Erro inesperado |

**Formato de erro:**
```json
{
  "tipo": "Recurso não encontrado",
  "mensagem": "Usuário não encontrado(a) com id: 999",
  "timestamp": "2024-10-17T14:30:00",
  "detalhes": {}
}
```