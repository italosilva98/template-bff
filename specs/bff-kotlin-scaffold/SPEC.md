# SPEC — Kotlin BFF Scaffold

## 1. Contexto

Este projeto é um scaffold/template de BFF — Backend-for-Frontend — em Kotlin, criado para acelerar o desenvolvimento de novos BFFs pelo time.

O objetivo não é criar um framework interno complexo, nem uma arquitetura hexagonal completa. O objetivo é fornecer uma base simples, padronizada, segura, bem documentada e fácil de evoluir.

O scaffold deve permitir que um desenvolvedor júnior consiga criar uma nova rota seguindo exemplos claros de:

- Controller
- Use Case
- Client externo
- Mapper
- DTOs
- Tratamento de erro
- Testes
- Documentação

## 2. Objetivo principal

Criar um projeto Kotlin com Spring Boot para BFF usando arquitetura Clean-Lite por Feature.

A arquitetura deve seguir este fluxo:

```text
HTTP Request
  -> Controller
  -> UseCase
  -> External Client
  -> External API
```

A estrutura deve favorecer simplicidade, legibilidade, baixo acoplamento e velocidade de desenvolvimento.

## 3. Decisão arquitetural

Usar arquitetura Clean-Lite por Feature.

Não usar Clean Architecture completa.  
Não usar Hexagonal Architecture completa.  
Não criar abstrações excessivas.  
Não criar framework interno.

A organização principal deve ser por funcionalidade:

```text
features/
  example/
    api/
    application/
    client/
    mapper/
    model/
```

E os recursos compartilhados devem ficar em:

```text
shared/
  config/
  error/
  observability/
  security/
  documentation/
  resilience/
```

## 4. Stack técnica

Usar:

- Kotlin
- Java 25
- Spring Boot 3.x estável
- Gradle Kotlin DSL
- Spring Web MVC
- Spring Validation
- Spring Actuator
- Springdoc OpenAPI
- RestClient para chamadas HTTP externas
- Jackson Kotlin Module
- JUnit 5
- MockK
- Spring Boot Test
- WireMock ou MockWebServer para testes de client HTTP
- Dockerfile
- docker-compose.yml
- Detekt ou Ktlint, preferencialmente configurado de forma simples

Evitar neste primeiro scaffold:

- Banco de dados
- JPA
- Flyway
- Kafka
- SQS
- Cache distribuído
- Camada domain rica
- Repositories
- Ports e adapters formais
- Herança genérica para Controller, UseCase ou Client
- Framework interno próprio

## 5. Requisitos funcionais

### RF-001 — Estrutura inicial do projeto

O projeto deve conter:

```text
build.gradle.kts
settings.gradle.kts
Dockerfile
docker-compose.yml
README.md
docs/
specs/
src/main/kotlin
src/test/kotlin
```

O pacote base deve ser:

```text
br.com.company.bff
```

Pode usar outro nome caso necessário, mas manter tudo consistente.

### RF-002 — Classe principal

Criar a classe principal:

```text
BffApplication.kt
```

Com bootstrap padrão do Spring Boot.

### RF-003 — Feature de exemplo

Criar uma feature chamada `example` para servir como modelo.

A feature deve conter:

```text
features/example/api/ExampleController.kt
features/example/api/request/ExampleRequest.kt
features/example/api/response/ExampleResponse.kt
features/example/application/GetExampleUseCase.kt
features/example/client/ExampleApiClient.kt
features/example/client/ExampleApiProperties.kt
features/example/client/response/ExampleApiResponse.kt
features/example/mapper/ExampleMapper.kt
features/example/model/ExampleView.kt
```

A rota de exemplo deve ser:

```http
GET /api/v1/examples/{id}
```

Ela deve:

1. Receber um `id` pela URL.
2. Chamar o `GetExampleUseCase`.
3. O use case deve chamar o `ExampleApiClient`.
4. O client deve simular uma chamada para uma API externa configurada por properties.
5. O mapper deve converter a resposta externa em resposta do BFF.
6. O controller deve retornar `ExampleResponse`.

### RF-004 — Controller

O controller deve:

- Ser pequeno.
- Não conter regra de negócio.
- Não chamar clients externos diretamente.
- Não fazer mapping complexo.
- Delegar o fluxo para um UseCase.

Exemplo conceitual:

```kotlin
@RestController
@RequestMapping("/api/v1/examples")
class ExampleController(
    private val getExampleUseCase: GetExampleUseCase
) {
    @GetMapping("/{id}")
    fun getById(@PathVariable id: String): ResponseEntity<ExampleResponse> {
        return ResponseEntity.ok(getExampleUseCase.execute(id))
    }
}
```

### RF-005 — Use Case

O use case deve:

- Orquestrar a funcionalidade.
- Chamar um ou mais clients.
- Aplicar somente regras leves de composição/adaptação.
- Usar mapper para transformação de dados.
- Ser fácil de testar.

Não criar interface para use case neste primeiro momento, a menos que exista necessidade real.

Exemplo conceitual:

```kotlin
@Service
class GetExampleUseCase(
    private val exampleApiClient: ExampleApiClient,
    private val exampleMapper: ExampleMapper
) {
    fun execute(id: String): ExampleResponse {
        val externalResponse = exampleApiClient.getById(id)
        val view = exampleMapper.toView(externalResponse)
        return exampleMapper.toResponse(view)
    }
}
```

### RF-006 — Client HTTP externo

O client externo deve:

- Usar `RestClient`.
- Receber base URL via `application.yml`.
- Ter timeout configurado.
- Ter tratamento claro de erro.
- Não vazar detalhes técnicos da API externa para o controller.
- Retornar DTOs próprios da API externa.

Exemplo de properties:

```yaml
external-apis:
  example-api:
    base-url: http://localhost:9090
    timeout: 3s
```

Criar:

```text
ExampleApiProperties.kt
ExampleApiClient.kt
ExampleApiResponse.kt
```

### RF-007 — Configuração de HTTP Client

Criar configuração compartilhada em:

```text
shared/config/RestClientConfig.kt
```

Essa configuração deve permitir criar clients com:

- baseUrl
- timeout
- interceptador de correlation id
- logs controlados
- headers padrão

Não criar uma abstração genérica complexa demais.

### RF-008 — Tratamento global de erros

Criar pacote:

```text
shared/error
```

Com:

```text
ApiException.kt
ExternalApiException.kt
ResourceNotFoundException.kt
ErrorResponse.kt
GlobalExceptionHandler.kt
```

O erro retornado pela API deve seguir um padrão simples:

```json
{
  "timestamp": "2026-05-10T10:15:30",
  "status": 404,
  "error": "RESOURCE_NOT_FOUND",
  "message": "Example not found",
  "path": "/api/v1/examples/123",
  "correlationId": "abc-123"
}
```

O `GlobalExceptionHandler` deve tratar pelo menos:

- validação
- recurso não encontrado
- erro de integração externa
- erro inesperado

### RF-009 — Observabilidade

Criar pacote:

```text
shared/observability
```

Com:

```text
CorrelationIdFilter.kt
LoggingConfig.kt
```

O scaffold deve:

- Gerar ou propagar header `X-Correlation-Id`.
- Incluir correlation id nos logs.
- Retornar correlation id em respostas de erro.
- Usar logs estruturados quando possível.
- Evitar logar dados sensíveis.

### RF-010 — Health check

Ativar Spring Actuator.

Expor pelo menos:

```text
/actuator/health
/actuator/info
```

Configurar o mínimo necessário no `application.yml`.

### RF-011 — OpenAPI/Swagger

Adicionar Springdoc OpenAPI.

A documentação deve ficar disponível em:

```text
/swagger-ui.html
```

ou rota equivalente do Springdoc.

Criar configuração em:

```text
shared/documentation/OpenApiConfig.kt
```

A documentação deve conter:

- nome da API
- descrição
- versão
- contato ou área responsável genérica

### RF-012 — Segurança

O scaffold deve ter uma estrutura inicial para segurança, mas sem acoplar a um provedor específico.

Criar pacote:

```text
shared/security
```

Com:

```text
AuthenticatedUser.kt
CurrentUserProvider.kt
SecurityConfig.kt
```

A configuração deve ser simples.

Para ambiente local, permitir execução sem autenticação real.

Deixar documentado no README onde integrar:

- OAuth2 Resource Server
- JWT
- propagação de token
- autorização por roles/scopes

Não implementar integração real com Cognito, Keycloak, Azure AD ou provedor corporativo neste primeiro scaffold.

### RF-013 — Resiliência

Criar pacote:

```text
shared/resilience
```

Incluir configuração simples para:

- timeout
- retry controlado
- circuit breaker opcional

Usar Resilience4j apenas se a configuração ficar simples e compreensível.

Não aplicar retry automaticamente em tudo.

Documentar que retry deve ser usado apenas em chamadas idempotentes.

### RF-014 — Arquivos de configuração

Criar:

```text
src/main/resources/application.yml
src/main/resources/application-local.yml
src/test/resources/application-test.yml
```

`application.yml` deve conter defaults seguros.

`application-local.yml` deve ser simples para rodar local.

`application-test.yml` deve ser usado nos testes.

### RF-015 — Dockerfile

Criar Dockerfile multi-stage para build e execução.

O container final deve usar JRE compatível com Java 25.

O app deve expor a porta:

```text
8080
```

### RF-016 — docker-compose

Criar `docker-compose.yml` com:

- serviço do BFF
- serviço WireMock ou equivalente simulando API externa

A API externa mockada deve responder ao endpoint usado pelo `ExampleApiClient`.

### RF-017 — Testes

Criar testes para:

```text
GetExampleUseCaseTest
ExampleMapperTest
ExampleControllerTest
ExampleApiClientTest
GlobalExceptionHandlerTest, se fizer sentido
```

Os testes devem validar:

- fluxo feliz
- erro de recurso não encontrado
- erro de API externa
- contrato básico do controller
- mapping entre DTO externo e response do BFF

Preferir testes simples, legíveis e úteis.

Não criar testes frágeis apenas para aumentar cobertura.

### RF-018 — README

Criar README completo com:

1. O que é o projeto.
2. Quando usar este scaffold.
3. Quando não usar este scaffold.
4. Arquitetura escolhida.
5. Estrutura de pastas.
6. Como rodar localmente.
7. Como rodar com Docker Compose.
8. Como executar testes.
9. Como criar uma nova feature.
10. Como criar um novo client externo.
11. Como tratar erros.
12. Como configurar variáveis.
13. Como integrar autenticação real.
14. Checklist para Pull Request.
15. Exemplos de prompts para usar com GitHub Copilot.

### RF-019 — ADR

Criar pasta:

```text
docs/adr
```

Com o arquivo:

```text
0001-architecture-clean-lite-by-feature.md
```

O ADR deve explicar:

- contexto
- decisão
- alternativas consideradas
- por que não usar Hexagonal completa
- por que não usar MVC solto
- consequências positivas
- riscos
- como evoluir a arquitetura se o BFF crescer

### RF-020 — Copilot instructions

Criar arquivo:

```text
.github/copilot-instructions.md
```

Esse arquivo deve orientar o Copilot a respeitar a arquitetura do projeto.

Deve conter instruções como:

- Não chamar client diretamente no controller.
- Criar uma pasta por feature.
- Criar UseCase para cada caso de uso.
- Manter DTOs externos separados dos DTOs expostos pelo BFF.
- Não criar abstrações genéricas sem necessidade.
- Não criar repositories sem banco de dados.
- Não criar domain layer rica sem regra de domínio real.
- Sempre criar testes junto com novas features.
- Sempre atualizar README quando adicionar novo padrão relevante.

### RF-021 — Prompt files para Copilot

Criar pasta:

```text
.github/prompts
```

Com arquivos:

```text
create-new-feature.prompt.md
create-new-client.prompt.md
create-controller-usecase-client.prompt.md
review-bff-architecture.prompt.md
generate-tests.prompt.md
```

Esses prompts devem ajudar devs júnior a evoluírem o BFF seguindo o padrão.

## 6. Requisitos não funcionais

### RNF-001 — Simplicidade

O projeto deve ser fácil de entender.

Um dev júnior deve conseguir criar uma nova rota usando a feature de exemplo como referência.

### RNF-002 — Baixo acoplamento

Controllers não devem depender de clients externos.

Clients externos não devem depender de controllers.

Shared não deve depender de features.

### RNF-003 — Testabilidade

Use cases e mappers devem ser facilmente testáveis com unit tests.

Clients devem ser testáveis com mock HTTP.

Controllers devem ser testáveis com Spring MVC test.

### RNF-004 — Segurança

O projeto não deve:

- logar tokens
- logar dados sensíveis
- hardcodar secrets
- expor stacktrace em resposta
- usar configuração insegura por padrão

### RNF-005 — Observabilidade

Toda request deve ter correlation id.

Erros devem ser rastreáveis por correlation id.

### RNF-006 — Manutenibilidade

Evitar:

- classes genéricas demais
- herança desnecessária
- overengineering
- acoplamento com frameworks internos inexistentes
- padrões que dificultem entendimento

### RNF-007 — Evolução futura

A arquitetura deve permitir evoluir para algo mais robusto caso o BFF cresça.

Exemplo de evolução possível:

```text
application/
domain/
ports/
adapters/
```

Mas essa evolução não deve ser implementada agora.

## 7. Regras arquiteturais obrigatórias

### Regra 1

Controller nunca chama client externo diretamente.

### Regra 2

UseCase coordena o fluxo da funcionalidade.

### Regra 3

Client externo só conhece detalhes da API externa.

### Regra 4

DTO externo não deve ser retornado diretamente pelo controller.

### Regra 5

Mapper deve isolar transformação de dados.

### Regra 6

Não criar repository sem banco de dados.

### Regra 7

Não criar camada `domain` rica se não houver regra de domínio real.

### Regra 8

Não criar interfaces para tudo automaticamente.

Criar interface apenas quando houver necessidade real, como:

- múltiplas implementações
- mock complexo
- integração variável
- isolamento necessário

### Regra 9

Não criar classes base genéricas como:

```text
BaseController
BaseUseCase
BaseClient
BaseResponse
```

A menos que a necessidade seja comprovada.

### Regra 10

Toda nova feature deve ter pelo menos:

```text
Controller
UseCase
Client, se houver integração externa
Mapper
Request/Response DTOs
Testes
```

## 8. Estrutura esperada

A estrutura final deve se aproximar de:

```text
.
├── .github
│   ├── copilot-instructions.md
│   └── prompts
│       ├── create-new-feature.prompt.md
│       ├── create-new-client.prompt.md
│       ├── create-controller-usecase-client.prompt.md
│       ├── review-bff-architecture.prompt.md
│       └── generate-tests.prompt.md
│
├── docs
│   └── adr
│       └── 0001-architecture-clean-lite-by-feature.md
│
├── specs
│   └── bff-kotlin-scaffold
│       └── SPEC.md
│
├── src
│   ├── main
│   │   ├── kotlin
│   │   │   └── br/com/company/bff
│   │   │       ├── BffApplication.kt
│   │   │       ├── features
│   │   │       │   └── example
│   │   │       │       ├── api
│   │   │       │       │   ├── ExampleController.kt
│   │   │       │       │   ├── request
│   │   │       │       │   │   └── ExampleRequest.kt
│   │   │       │       │   └── response
│   │   │       │       │       └── ExampleResponse.kt
│   │   │       │       ├── application
│   │   │       │       │   └── GetExampleUseCase.kt
│   │   │       │       ├── client
│   │   │       │       │   ├── ExampleApiClient.kt
│   │   │       │       │   ├── ExampleApiProperties.kt
│   │   │       │       │   └── response
│   │   │       │       │       └── ExampleApiResponse.kt
│   │   │       │       ├── mapper
│   │   │       │       │   └── ExampleMapper.kt
│   │   │       │       └── model
│   │   │       │           └── ExampleView.kt
│   │   │       └── shared
│   │   │           ├── config
│   │   │           ├── documentation
│   │   │           ├── error
│   │   │           ├── observability
│   │   │           ├── resilience
│   │   │           └── security
│   │   └── resources
│   │       ├── application.yml
│   │       └── application-local.yml
│   └── test
│       ├── kotlin
│       └── resources
│           └── application-test.yml
│
├── build.gradle.kts
├── settings.gradle.kts
├── Dockerfile
├── docker-compose.yml
└── README.md
```

## 9. Critérios de aceite

O projeto será considerado pronto quando:

- Compilar com sucesso.
- Rodar localmente.
- Rodar via Docker Compose.
- Expor `/actuator/health`.
- Expor Swagger/OpenAPI.
- Ter a rota `GET /api/v1/examples/{id}` funcionando.
- Ter mock de API externa local funcionando.
- Ter tratamento global de erro funcionando.
- Ter correlation id nas requests.
- Ter testes unitários e de controller/client.
- Ter README explicando como criar nova feature.
- Ter ADR justificando a arquitetura.
- Ter `.github/copilot-instructions.md`.
- Ter prompt files para o Copilot.
- Não conter abstrações excessivas.
- Não conter banco de dados.
- Não conter repositories.
- Não conter Clean Architecture/Hexagonal completa.

## 10. Plano de implementação sugerido

Implementar em fases.

### Fase 1 — Bootstrap

Criar projeto Kotlin + Spring Boot + Gradle.

Criar:

```text
BffApplication.kt
application.yml
application-local.yml
application-test.yml
Dockerfile
docker-compose.yml
```

### Fase 2 — Shared foundation

Criar pacotes compartilhados:

```text
shared/config
shared/error
shared/observability
shared/documentation
shared/security
shared/resilience
```

### Fase 3 — Feature example

Criar feature `example` completa com:

```text
Controller
UseCase
Client
Properties
Mapper
Model
DTOs
```

### Fase 4 — Testes

Criar testes para:

```text
Controller
UseCase
Mapper
Client
Error Handler
```

### Fase 5 — Documentação

Criar:

```text
README.md
ADR
copilot-instructions.md
prompt files
```

### Fase 6 — Validação

Executar:

```bash
./gradlew clean build
./gradlew test
docker compose up --build
```

Corrigir erros encontrados.

## 11. Restrições importantes para o Copilot

Ao implementar, siga estas restrições:

1. Não inventar requisitos fora da spec.
2. Não adicionar banco de dados.
3. Não adicionar Kafka, SQS ou mensageria.
4. Não adicionar autenticação real com provedor externo.
5. Não criar arquitetura hexagonal completa.
6. Não criar Clean Architecture purista.
7. Não criar camada domain sem necessidade.
8. Não criar interfaces desnecessárias.
9. Não criar classes base genéricas.
10. Não deixar código sem teste.
11. Não deixar README superficial.
12. Não ignorar tratamento de erros.
13. Não retornar DTO externo diretamente pela API do BFF.
14. Não deixar valores sensíveis hardcoded.
15. Não usar dependências experimentais sem necessidade.

## 12. Resultado esperado

Ao final, entregar um scaffold que sirva como referência oficial para novos BFFs Kotlin do time.

O projeto deve ser simples, didático, produtivo e seguro.

O dev deve conseguir criar uma nova feature seguindo este fluxo:

```text
1. Criar DTOs de request/response
2. Criar client externo, se necessário
3. Criar mapper
4. Criar use case
5. Criar controller
6. Criar testes
7. Atualizar documentação, se necessário
```

O scaffold deve ensinar pelo exemplo.
