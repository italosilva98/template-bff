# Prompt — Criar rota BFF completa

Você é um desenvolvedor sênior Kotlin/Spring Boot.

Leia o arquivo de instruções de arquitetura:

```text
.github/copilot-instructions.md
```

Use a feature `example` em `src/main/kotlin/itau/template/bff/features/example/` como referência de código.

## Rota a ser criada

Preencha antes de executar:

```text
Nome da feature (ex: customers, orders, accounts):
Endpoint BFF (ex: GET /api/v1/customers/{id}):
Método HTTP:
API externa chamada (ex: customer-api):
Endpoint na API externa (ex: GET /v1/customers/{id}):
Headers necessários na chamada externa (se houver):
```

## Payload

```text
Request BFF (se houver body):
Response BFF esperado:
Response da API externa esperado:
```

## Cenários de erro

```text
Erros esperados da API externa:
(ex: 404 → ResourceNotFoundException, 5xx → ExternalApiException)
```

---

## O que deve ser criado

### Estrutura de arquivos

```text
features/{nome}/
  api/
    {Nome}Controller.kt                    ← @RestController
    response/
      {Nome}Response.kt                   ← DTO público (retornado ao front-end)
  application/
    Get{Nome}UseCase.kt                   ← @Service (ou Create/Update conforme verbo)
  client/
    {ExternalApi}Client.kt               ← @Component, RestClient
    {ExternalApi}Properties.kt           ← @ConfigurationProperties
    response/
      {ExternalApi}Response.kt           ← DTO externo (nunca exposto pelo controller)
  mapper/
    {Nome}Mapper.kt                      ← @Component
  model/
    {Nome}View.kt                        ← model interno (data class)
```

### Configuração em application.yml

Adicionar bloco:

```yaml
external-apis:
  {nome-da-api}:
    base-url: http://localhost:{porta}
    timeout: 3s
```

### Testes

Criar em `src/test/kotlin/itau/template/bff/features/{nome}/`:

```text
mapper/
  {Nome}MapperTest.kt          ← JUnit 5, sem mock, teste unitário puro
application/
  Get{Nome}UseCaseTest.kt      ← MockK para o client, mapper real
client/
  {ExternalApi}ClientTest.kt   ← WireMock, @RegisterExtension, porta dinâmica
api/
  {Nome}ControllerTest.kt      ← MockMvc standaloneSetup + GlobalExceptionHandler
```

---

## Regras obrigatórias

- Controller nunca chama Client diretamente
- Controller não contém lógica de negócio
- DTO externo nunca aparece como response do controller
- Erros externos convertidos para `ResourceNotFoundException` (404) ou `ExternalApiException` (502)
- Properties carregadas via `@ConfigurationProperties`, nunca hardcoded
- Sem `!!` (null assertion) no código
- Correlation ID propagado automaticamente (já feito pelo `RestClientConfig`)

---

## Como tratar campos opcionais

Se a API externa retornar campos que podem ser nulos:

```kotlin
// No mapper — converter null para valor padrão explícito
description = apiResponse.description ?: ""

// Nunca deixar !! no código
name = apiResponse.name!!  // ❌ proibido
name = apiResponse.name ?: throw ResourceNotFoundException("...")  // ✅
```

---

## Assumptions

Se o contrato (request/response) não for informado:

1. Criar DTOs mínimos com os campos mais prováveis
2. Adicionar `// TODO: ajustar contrato conforme API real` em cada DTO
3. Informar no resumo final quais campos foram assumidos e por quê

---

## Resumo final esperado

Ao terminar, listar:

1. Arquivos criados
2. Trecho de `application.yml` para adicionar
3. Assumptions feitas (se houver)
4. Como testar a rota localmente
