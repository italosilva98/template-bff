# BFF Kotlin Scaffold

Scaffold de BFF — Backend-for-Frontend — em **Kotlin + Spring Boot**, seguindo a arquitetura **Clean-Lite por Feature**.

Criado para ser o ponto de partida de novos BFFs no time. Simples o suficiente para um dev júnior entender e evoluir.

---

## 1. O que é o projeto

Este projeto é um template pronto para uso como BFF. Ele inclui:

- Estrutura de código organizada por feature
- Client HTTP configurado com timeout e correlation ID
- Tratamento centralizado de erros
- Filtro de correlation ID com MDC
- Configuração básica de segurança (stateless, HTTP Basic desabilitado por padrão)
- Documentação OpenAPI/Swagger
- Actuator com health check
- Feature de exemplo completa: `GET /api/v1/examples/{id}`
- Testes de todas as camadas (mapper, use case, client, controller)
- Dockerfile multi-stage e Docker Compose com mock da API externa

## 2. Quando usar este scaffold

- Criar um novo BFF que agrega e adapta respostas de APIs internas para o front-end
- Times que querem uma base padronizada sem reinventar configurações de projeto
- Situações onde o BFF **não é dono do domínio** — apenas orquestra e adapta
- Quando a velocidade de entrega e clareza para juniors são prioridade

## 3. Quando **não** usar este scaffold

- Se o projeto precisar de banco de dados e regras de domínio ricas → considere um serviço backend completo com Clean Architecture
- Se a equipe quiser Hexagonal completo com ports/adapters → este scaffold é deliberadamente mais simples
- Se o BFF for um proxy puro sem nenhuma lógica → um gateway (Kong, APISIX) pode ser suficiente

## 4. Arquitetura escolhida

**Clean-Lite por Feature**. Detalhes na [ADR-0001](docs/adr/0001-architecture-clean-lite-by-feature.md).

Cada feature segue este fluxo obrigatório:

```text
HTTP Request
  └── Controller          (recebe, valida entrada básica, retorna response)
        └── UseCase       (orquestra a funcionalidade)
              └── Client  (conhece a API externa)
                    └── External API
                          └── Mapper  (converte DTO externo → model interno → response público)
```

Regras que não podem ser quebradas:

- Controller **nunca** chama client externo diretamente
- Controller **não** contém regra de negócio
- DTO externo **nunca** é retornado direto pela API pública do BFF
- Mapper **isola** a transformação de dados

## 5. Estrutura de pastas

```text
src/main/kotlin/itau/template/bff/
├── BffApplication.kt
├── features/
│   └── example/                        ← feature de exemplo (copiar e adaptar)
│       ├── api/
│       │   ├── ExampleController.kt    ← endpoint público
│       │   └── response/
│       │       └── ExampleResponse.kt  ← DTO público (retornado ao front-end)
│       ├── application/
│       │   └── GetExampleUseCase.kt    ← orquestra controller ↔ client ↔ mapper
│       ├── client/
│       │   ├── ExampleApiClient.kt     ← faz a chamada HTTP
│       │   ├── ExampleApiProperties.kt ← configuração (base-url, timeout)
│       │   └── response/
│       │       └── ExampleApiResponse.kt ← DTO externo (nunca exposto)
│       ├── mapper/
│       │   └── ExampleMapper.kt        ← converte DTO externo → model → response
│       └── model/
│           └── ExampleView.kt          ← model interno da feature
└── shared/
    ├── config/
    │   ├── JacksonConfig.kt            ← configuração Jackson 3.x (Spring Boot 4)
    │   └── RestClientConfig.kt         ← factory de RestClient com timeout + correlation ID
    ├── documentation/
    │   └── OpenApiConfig.kt            ← configuração Swagger/OpenAPI
    ├── error/
    │   ├── ApiException.kt             ← exception base
    │   ├── ExternalApiException.kt     ← erros de APIs externas → 502
    │   ├── GlobalExceptionHandler.kt   ← @RestControllerAdvice
    │   ├── ErrorResponse.kt            ← formato padrão de erro
    │   └── ResourceNotFoundException.kt ← 404
    ├── observability/
    │   ├── CorrelationIdFilter.kt      ← gera/propaga X-Correlation-Id
    │   └── LoggingConfig.kt            ← ponto de extensão para log estruturado
    ├── resilience/
    │   └── ResilienceConfig.kt         ← ponto de extensão para Resilience4j
    └── security/
        ├── AuthenticatedUser.kt        ← modelo do usuário autenticado
        ├── CurrentUserProvider.kt      ← ponto de extensão para OAuth2/JWT
        └── SecurityConfig.kt           ← Spring Security stateless, sem CSRF
```

## 6. Como rodar localmente

**Pré-requisitos:** Java 21+ (JDK), Gradle (ou use o wrapper `./gradlew`)

```bash
# Compilar e rodar testes
./gradlew clean build

# Rodar a aplicação com perfil local
./gradlew bootRun --args='--spring.profiles.active=local'
```

Com o perfil `local`, a aplicação sobe na porta `8080` com:

- Credenciais dev: `dev` / `dev`
- Todos os endpoints do Actuator expostos
- Log em nível DEBUG

Verificar que está no ar:

```bash
curl http://localhost:8080/actuator/health
```

Swagger disponível em: `http://localhost:8080/swagger-ui.html`

> **Atenção:** a feature `example` chama `http://localhost:9090`. Para testar a rota de exemplo localmente sem Docker, suba o WireMock separadamente (veja a seção Docker) ou configure `external-apis.example-api.base-url` para uma URL real.

## 7. Como rodar com Docker Compose

O Docker Compose sobe dois serviços: o BFF e um mock da API externa (WireMock).

```bash
# Build e subir tudo
docker compose up --build

# Ou em background
docker compose up --build -d

# Ver logs do BFF
docker compose logs -f bff

# Derrubar
docker compose down
```

Testar a aplicação rodando:

```bash
# Fluxo feliz — retorna o item mockado
curl http://localhost:8080/api/v1/examples/42

# 404 — item não encontrado
curl http://localhost:8080/api/v1/examples/not-found

# Health check
curl http://localhost:8080/actuator/health

# Verificar WireMock direto
curl http://localhost:9090/__admin/health
```

Para adicionar novos stubs WireMock, crie arquivos em:

```text
wiremock/mappings/   ← arquivos de mapeamento (JSON)
wiremock/__files/    ← arquivos de response body
```

## 8. Como executar e criar testes

### Rodar os testes existentes

```bash
./gradlew test

# Com relatório HTML
./gradlew test && open build/reports/tests/test/index.html
```

### Estrutura de testes por camada

| Camada | Tipo | Ferramenta |
|---|---|---|
| Mapper | Teste unitário puro | JUnit 5 + Kotlin |
| UseCase | Teste unitário com mock | MockK |
| Client | Teste de integração | WireMock |
| Controller | Teste de slice MVC | MockMvc + standaloneSetup |

### Criar testes para uma nova feature

Use o prompt do Copilot:

1. No VS Code, abra o **Copilot Chat** (⌃⌘I / Ctrl+Alt+I)
2. Clique em **"Attach Context"** → **"Prompt..."**
3. Selecione `.github/prompts/generate-tests.prompt.md`
4. Preencha os campos pedidos e envie

O prompt vai criar testes de todas as camadas seguindo o mesmo padrão da feature `example`.

## 9. Como criar uma nova feature

Uma feature é um conjunto de arquivos que implementa uma rota completa no BFF, desde o controller até a chamada à API externa.

**Exemplo de feature:** `GET /api/v1/customers/{id}` que chama `customer-api`

### Passos rápidos

1. Copie a pasta `features/example/` como referência
2. Use o prompt do Copilot para gerar o código:
   - No VS Code, abra o **Copilot Chat**
   - Clique em **"Attach Context"** → **"Prompt..."**
   - Selecione `.github/prompts/create-new-feature.prompt.md`
   - Preencha: nome da feature, endpoint, método HTTP, request/response esperados, APIs externas, cenários de erro
3. Revise o código gerado seguindo o [Checklist para PR](#14-checklist-para-pull-request)

### Checklist rápido ao criar uma feature

- [ ] Controller chama apenas o UseCase
- [ ] UseCase chama apenas o Client e o Mapper
- [ ] DTO externo nunca aparece como response do controller
- [ ] Erros externos convertidos para `ExternalApiException` ou `ResourceNotFoundException`
- [ ] Testes criados para todas as camadas
- [ ] Properties em `application.yml` (não hardcoded)

## 10. Como criar um novo client externo

Um client é responsável por chamar uma API externa. Ele fica dentro da pasta `client/` da sua feature.

Use o prompt do Copilot:

1. No VS Code, abra o **Copilot Chat**
2. Clique em **"Attach Context"** → **"Prompt..."**
3. Selecione `.github/prompts/create-new-client.prompt.md`
4. Preencha: nome da API, base URL, endpoints, headers, timeout, erros conhecidos

### O que o client deve sempre fazer

```kotlin
// ✅ Correto — tratar 404 como ResourceNotFoundException
.onStatus({ it.value() == 404 }) { _, _ ->
    throw ResourceNotFoundException("Recurso não encontrado: $id")
}

// ✅ Correto — tratar 5xx como ExternalApiException  
.onStatus({ it.is5xxServerError }) { _, response ->
    throw ExternalApiException("Falha na API externa", response.statusCode.value(), null)
}

// ❌ Errado — não propagar DTO externo diretamente
fun getById(id: String): ExternalApiResponse   // evitar retornar isso no controller
```

### Configuração em application.yml

```yaml
external-apis:
  minha-api:
    base-url: http://minha-api.interna
    timeout: 5s
```

Properties class:

```kotlin
@ConfigurationProperties(prefix = "external-apis.minha-api")
data class MinhaApiProperties(
    val baseUrl: String,
    val timeout: Duration = Duration.ofSeconds(3)
)
```

## 11. Como tratar erros

O BFF usa um tratamento centralizado via `GlobalExceptionHandler`. **Não crie try/catch espalhados pelo código.**

### Exceptions disponíveis

| Exception | Status HTTP | Quando usar |
|---|---|---|
| `ResourceNotFoundException` | 404 | Recurso não encontrado na API externa |
| `ExternalApiException` | 502 | Erro inesperado na API externa (5xx, timeout) |
| `ApiException` | configurável | Outros erros de negócio |

### Como lançar no client

```kotlin
// 404 da API externa → 404 do BFF
throw ResourceNotFoundException("Example not found: $id")

// 5xx da API externa → 502 do BFF
throw ExternalApiException("Falha ao consultar example-api", statusCode, cause)
```

### Formato padrão de resposta de erro

```json
{
  "timestamp": "2026-05-10T14:30:00",
  "status": 404,
  "error": "RESOURCE_NOT_FOUND",
  "message": "Example not found: 999",
  "path": "/api/v1/examples/999",
  "correlationId": "a1b2c3d4-..."
}
```

O `correlationId` é automaticamente injetado pelo `CorrelationIdFilter` — lido do header `X-Correlation-Id` da request ou gerado automaticamente se ausente.

## 12. Como configurar variáveis

### application.yml (padrão para todos os ambientes)

```yaml
external-apis:
  example-api:
    base-url: http://localhost:9090   # sobrescrever por ambiente
    timeout: 3s
```

### Sobrescrever por ambiente

Via variável de ambiente (recomendado para Docker/K8s):

```bash
export EXTERNAL_APIS_EXAMPLE_API_BASE_URL=https://api.producao.interna
export EXTERNAL_APIS_EXAMPLE_API_TIMEOUT=5s
```

Via perfil Spring:

```yaml
# application-prod.yml
external-apis:
  example-api:
    base-url: https://api.producao.interna
    timeout: 5s
```

Ativar o perfil:

```bash
SPRING_PROFILES_ACTIVE=prod java -jar app.jar
# ou
./gradlew bootRun --args='--spring.profiles.active=prod'
```

### Nunca faça

```yaml
# ❌ Não coloque secrets em application.yml
external-apis:
  minha-api:
    token: meu-token-secreto   # ERRADO — use variáveis de ambiente ou Vault
```

## 13. Como integrar autenticação real

O scaffold vem com segurança básica configurada em `SecurityConfig.kt`: stateless, CSRF desabilitado, sem HTTP Basic.

Para integrar OAuth2/JWT real:

### Passo 1 — Adicionar dependência

```kotlin
// build.gradle.kts
implementation("org.springframework.boot:spring-boot-starter-oauth2-resource-server")
```

### Passo 2 — Configurar application.yml

```yaml
spring:
  security:
    oauth2:
      resourceserver:
        jwt:
          issuer-uri: https://seu-provedor-identidade/.well-known/openid-configuration
```

### Passo 3 — Atualizar SecurityConfig.kt

```kotlin
@Bean
fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {
    http
        .authorizeHttpRequests { auth ->
            auth
                .requestMatchers("/actuator/health", "/v3/api-docs/**", "/swagger-ui/**").permitAll()
                .anyRequest().authenticated()   // ← ativar aqui
        }
        .oauth2ResourceServer { oauth2 -> oauth2.jwt(withDefaults()) }
        .sessionManagement { it.sessionCreationPolicy(STATELESS) }
        .csrf { it.disable() }
    return http.build()
}
```

### Passo 4 — Implementar CurrentUserProvider.kt

```kotlin
@Component
class CurrentUserProvider(private val securityContext: SecurityContextHolderStrategy) {
    fun current(): AuthenticatedUser? {
        val jwt = securityContext.context.authentication?.principal as? Jwt ?: return null
        return AuthenticatedUser(
            id = jwt.subject,
            name = jwt.getClaimAsString("name") ?: "unknown",
            roles = jwt.getClaimAsStringList("roles") ?: emptyList()
        )
    }
}
```

## 14. Checklist para Pull Request

Antes de abrir um PR, verifique:

### Arquitetura

- [ ] Controller chama apenas UseCase (não chama client diretamente)
- [ ] Controller não contém lógica de negócio
- [ ] DTO externo não é retornado pelo controller
- [ ] Mapper isola a transformação de dados
- [ ] Novas propriedades estão em `application.yml` (não hardcoded)

### Código

- [ ] Sem `TODO` sem contexto (adicione número de ticket ou descrição clara)
- [ ] Sem secrets ou tokens no código ou no YAML
- [ ] Sem logging de dados sensíveis (tokens, documentos, senhas)
- [ ] Correlation ID propagado nas chamadas externas (já feito pelo `RestClientConfig`)

### Testes

- [ ] Testes de mapper (unitário puro)
- [ ] Testes de use case (com MockK)
- [ ] Testes de client (com WireMock)
- [ ] Testes de controller (com MockMvc standaloneSetup)
- [ ] Todos os testes passando: `./gradlew test`

### Build

- [ ] `./gradlew clean build` sem erros
- [ ] `docker compose up --build` sobe sem erros (se mudou Dockerfile ou dependências)

## 15. Exemplos de prompts para usar com GitHub Copilot

### Criar uma nova feature completa

```
Use o prompt create-new-feature.

Crie a feature customers com a rota GET /api/v1/customers/{id}.

Ela deve chamar a customer-api no endpoint GET /v1/customers/{id}.

Response do BFF:
{
  "id": "123",
  "name": "James",
  "document": "12345678900",
  "status": "ACTIVE"
}

Cenários de erro:
- 404 na API externa → retornar 404 com mensagem "Customer not found: {id}"
- 5xx na API externa → retornar 502
```

### Criar apenas um client externo

```
Use o prompt create-new-client.

Crie o client para a order-api dentro da feature orders.

Endpoint: GET /orders/{orderId}
Timeout: 5s
Erros: 404 → ResourceNotFoundException, 5xx → ExternalApiException
```

### Gerar testes

```
Use o prompt generate-tests.

Gere testes completos para a feature customers:
- CustomerMapper
- GetCustomerUseCase
- CustomerApiClient (WireMock)
- CustomerController (MockMvc)
```

### Revisão de arquitetura

```
Use o prompt review-bff-architecture.

Revise a feature orders verificando:
- Se o controller chama diretamente o client
- Se DTOs externos são expostos
- Se erros externos são tratados corretamente
```

---

## Stack

| Componente | Versão |
|---|---|
| Kotlin | 2.2.21 |
| Spring Boot | 4.0.x |
| Java (target JVM) | 24 |
| Gradle | 9.x |
| SpringDoc OpenAPI | 3.0.x |
| WireMock (testes) | 3.9.x |
| MockK | 1.13.x |

## Links úteis (quando rodando)

| Recurso | URL |
|---|---|
| Swagger UI | http://localhost:8080/swagger-ui.html |
| OpenAPI JSON | http://localhost:8080/v3/api-docs |
| Health | http://localhost:8080/actuator/health |
| Info | http://localhost:8080/actuator/info |

## Como usar

1. Crie um repositório vazio para o scaffold.
2. Copie todo o conteúdo deste kit para a raiz do repositório.
3. Abra o repositório no VS Code ou IntelliJ com GitHub Copilot Agent habilitado.
4. Use preferencialmente o modelo **Claude Sonnet 4.6** para a geração inicial.
5. Envie o prompt principal abaixo para o Copilot Agent:

```md
Você é um arquiteto de software sênior especialista em Kotlin, Spring Boot e criação de scaffolds corporativos.

Leia integralmente o arquivo:

`specs/bff-kotlin-scaffold/SPEC.md`

Implemente o projeto exatamente conforme a spec.

Prioridades:

1. Simplicidade.
2. Clareza para desenvolvedor júnior.
3. Arquitetura Clean-Lite por Feature.
4. Código funcional.
5. Testes úteis.
6. Documentação completa.
7. Nada de overengineering.

Não crie arquitetura hexagonal completa.
Não crie Clean Architecture purista.
Não adicione banco de dados.
Não adicione repositories.
Não crie interfaces desnecessárias.
Não crie classes base genéricas.

Implemente em fases:

1. Bootstrap do projeto.
2. Shared foundation.
3. Feature example.
4. Testes.
5. Docker e docker-compose.
6. README, ADR e arquivos de prompt para Copilot.
7. Validação final.

Ao final, execute ou indique os comandos necessários para validar:

```bash
./gradlew clean build
./gradlew test
docker compose up --build
```

Se algum requisito da spec não puder ser implementado, explique claramente o motivo e proponha a alternativa mais simples.
```

## Modelo recomendado

- Geração inicial: **Claude Sonnet 4.6**
- Revisão arquitetural: **GPT-5.4**
- Refatorações específicas: **Claude Sonnet 4.6**
- Revisão final: **GPT-5.4**

## Conteúdo do kit

```text
.
├── README.md
├── CHECKLIST.md
├── specs
│   └── bff-kotlin-scaffold
│       └── SPEC.md
├── docs
│   ├── adr
│   │   └── 0001-architecture-clean-lite-by-feature.md
│   └── architecture
│       └── clean-lite-by-feature.md
└── .github
    ├── copilot-instructions.md
    └── prompts
        ├── 00-generate-project-from-spec.prompt.md
        ├── 01-bootstrap-project.prompt.md
        ├── 02-create-shared-foundation.prompt.md
        ├── 03-create-example-feature.prompt.md
        ├── 04-generate-tests.prompt.md
        ├── 05-create-documentation.prompt.md
        ├── 06-review-bff-architecture.prompt.md
        ├── 07-fix-review-findings.prompt.md
        ├── create-new-feature.prompt.md
        ├── create-new-client.prompt.md
        └── review-security-observability.prompt.md
```

## Filosofia do scaffold

Este scaffold não deve ser um framework interno.

Ele deve ensinar pelo exemplo e acelerar o desenvolvimento de BFFs com uma estrutura previsível:

```text
Controller -> UseCase -> Client -> External API
```

Com organização por feature:

```text
features/
  example/
    api/
    application/
    client/
    mapper/
    model/
```

E recursos compartilhados em:

```text
shared/
  config/
  error/
  observability/
  security/
  documentation/
  resilience/
```
