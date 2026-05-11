# GitHub Copilot Instructions — BFF Kotlin Scaffold

Este arquivo instrui o Copilot sobre os padrões deste projeto.

---

## Visão geral do projeto

BFF em **Kotlin + Spring Boot 4** usando arquitetura **Clean-Lite por Feature**.

Pacote base: `itau.template.bff`

Fluxo obrigatório:

```text
Controller → UseCase → Client externo → Mapper → Response
```

---

## Regras absolutas de arquitetura

**Sempre:**

- Controller chama apenas o UseCase
- UseCase orquestra: chama Client, recebe resposta, chama Mapper
- Client faz a chamada HTTP e trata status codes externos
- Mapper converte DTO externo → model interno → response público
- Erros externos → `ResourceNotFoundException` (404) ou `ExternalApiException` (502)
- Novos endpoints externos em `application.yml` (nunca hardcoded)
- Criar testes junto com a feature

**Nunca:**

- Controller chamar Client diretamente
- DTO externo aparecer como response do controller
- Criar interfaces sem múltiplas implementações reais
- Criar `BaseController`, `BaseUseCase` ou `BaseClient`
- Criar `domain/` ou `ports/` sem necessidade concreta
- Criar `Repository` sem banco de dados
- Logar tokens, senhas ou documentos
- Colocar secrets em YAML

---

## Estrutura de pacotes esperada para cada feature

```text
features/{nome}/
  api/
    {Nome}Controller.kt
    response/
      {Nome}Response.kt        ← DTO público do BFF
  application/
    Get{Nome}UseCase.kt        ← ou Create/Update conforme o verbo
  client/
    {ExternalApi}Client.kt
    {ExternalApi}Properties.kt
    response/
      {ExternalApi}Response.kt ← DTO externo (nunca exposto)
  mapper/
    {Nome}Mapper.kt
  model/
    {Nome}View.kt              ← model interno
```

---

## Padrão de código

### Controller

```kotlin
@RestController
@RequestMapping("/api/v1/{recursos}")
class {Nome}Controller(private val useCase: Get{Nome}UseCase) {

    @GetMapping("/{id}")
    fun getById(@PathVariable id: String): ResponseEntity<{Nome}Response> {
        val response = useCase.execute(id)
        return ResponseEntity.ok(response)
    }
}
```

### UseCase

```kotlin
@Service
class Get{Nome}UseCase(
    private val client: {ExternalApi}Client,
    private val mapper: {Nome}Mapper
) {
    fun execute(id: String): {Nome}Response {
        val apiResponse = client.getById(id)
        val view = mapper.toView(apiResponse)
        return mapper.toResponse(view)
    }
}
```

### Client

```kotlin
@Component
class {ExternalApi}Client(
    private val properties: {ExternalApi}Properties,
    private val restClientConfig: RestClientConfig
) {
    private val client: RestClient by lazy {
        restClientConfig.buildClient(properties.baseUrl, properties.timeout)
    }

    fun getById(id: String): {ExternalApi}Response {
        return client.get()
            .uri("/{recurso}/{id}", id)
            .retrieve()
            .onStatus({ it.value() == 404 }) { _, _ ->
                throw ResourceNotFoundException("{Nome} not found: $id")
            }
            .onStatus({ it.is5xxServerError }) { _, response ->
                throw ExternalApiException(
                    "Falha ao consultar {external-api}",
                    response.statusCode.value(),
                    null
                )
            }
            .body({ExternalApi}Response::class.java)
            ?: throw ResourceNotFoundException("{Nome} not found: $id")
    }
}
```

### Mapper

```kotlin
@Component
class {Nome}Mapper {

    fun toView(apiResponse: {ExternalApi}Response): {Nome}View {
        return {Nome}View(
            id = apiResponse.id,
            // mapear campos aqui
        )
    }

    fun toResponse(view: {Nome}View): {Nome}Response {
        return {Nome}Response(
            id = view.id,
            // mapear campos aqui
        )
    }
}
```

### Properties

```kotlin
@ConfigurationProperties(prefix = "external-apis.{nome-da-api}")
data class {ExternalApi}Properties(
    val baseUrl: String,
    val timeout: Duration = Duration.ofSeconds(3)
)
```

---

## Padrão de testes

### Mapper — teste unitário puro

```kotlin
class {Nome}MapperTest {
    private val mapper = {Nome}Mapper()

    @Test
    fun `should map api response to view`() { ... }
}
```

### UseCase — com MockK

```kotlin
class Get{Nome}UseCaseTest {
    private val client = mockk<{ExternalApi}Client>()
    private val mapper = {Nome}Mapper()
    private val useCase = Get{Nome}UseCase(client, mapper)

    @Test
    fun `should return response when resource exists`() { ... }
}
```

### Client — com WireMock

```kotlin
class {ExternalApi}ClientTest {
    @RegisterExtension
    val wireMock: WireMockExtension = WireMockExtension.newInstance()
        .options(wireMockConfig().dynamicPort())
        .build()
    // ...
}
```

### Controller — com MockMvc standaloneSetup

```kotlin
class {Nome}ControllerTest {
    private val useCase = mockk<Get{Nome}UseCase>()
    private val mockMvc = MockMvcBuilders
        .standaloneSetup({Nome}Controller(useCase))
        .setControllerAdvice(GlobalExceptionHandler())
        .build()
    // ...
}
```

---

## Tratamento de erros

Usar sempre as exceptions de `shared/error/`:

| Situação | Exception | HTTP |
|---|---|---|
| Recurso não encontrado na API externa | `ResourceNotFoundException` | 404 |
| Erro 5xx ou inesperado na API externa | `ExternalApiException` | 502 |
| Erro de negócio customizado | `ApiException(status)` | configurável |

Nunca criar try/catch espalhado. O `GlobalExceptionHandler` cuida do mapeamento.

---

## Criação de novas rotas BFF

Para criar uma nova rota completa, usar o prompt:

```text
.github/prompts/create-bff-route.prompt.md
```

Exemplo de solicitação:

```text
Use o prompt create-bff-route.

Crie a rota GET /api/v1/customers/{id} chamando a customer-api no endpoint GET /customers/{id}.

Response do BFF:
{
  "id": "123",
  "name": "James",
  "document": "12345678900",
  "status": "ACTIVE"
}

Erros: 404 → ResourceNotFoundException, 5xx → ExternalApiException
```

Quando request/response não forem informados: criar DTOs mínimos, adicionar `TODO` com contexto claro, informar assumptions no resumo final.

---

## Convenções gerais

- Kotlin idiomático: `data class`, `by lazy`, extension functions quando claras
- Sem `!!` (null assertion) — usar `?: throw` ou `?.let`
- Nomes em inglês; comentários explicativos em português são aceitáveis
- Um arquivo por classe (exceto sealed classes pequenas no mesmo arquivo)
- `application.yml` para configurações; `application-local.yml` para dev local
- Não adicionar dependências sem necessidade real

Quando o desenvolvedor solicitar a criação de uma nova rota, siga sempre o padrão Clean-Lite por Feature.

Toda rota deve seguir o fluxo:

```text
Controller -> UseCase -> Client externo -> Mapper -> Response
```

Regras obrigatórias:

- Controller nunca chama client externo diretamente.
- Controller não contém regra de negócio.
- Controller deve apenas receber a request, validar entrada básica, chamar o use case e retornar response.
- UseCase orquestra a funcionalidade.
- Client externo conhece detalhes da API externa.
- DTO externo nunca é retornado diretamente pela API pública.
- Mapper isola transformação de dados.
- Não criar repositories sem banco de dados.
- Não criar camada domain rica sem necessidade real.
- Não criar interfaces sem necessidade real.
- Não criar classes base genéricas como `BaseController`, `BaseUseCase` ou `BaseClient`.
- Sempre criar testes junto com a rota.
- Quando request/response não forem informados, não inventar contrato complexo; criar DTO mínimo, adicionar TODO e informar assumptions no resumo final.

Para criação de rotas completas, preferir usar o prompt file:

```text
.github/prompts/create-bff-route.prompt.md
```

## Exemplo de pedido esperado do desenvolvedor

```text
Use o prompt create-bff-route.

Crie a rota GET /api/v1/customers/{id} chamando a API customer-api no endpoint GET /customers/{id}.

A response do BFF deve retornar:
{
  "id": "123",
  "name": "James",
  "document": "12345678900",
  "status": "ACTIVE"
}
```

## Exemplo quando não houver payload informado

```text
Use o prompt create-bff-route.

Crie a rota GET /api/v1/orders/{orderId}/summary chamando a API order-api no endpoint GET /orders/{orderId}/summary.
```

Neste caso, como o payload não foi informado, crie DTOs mínimos, adicione TODOs claros nos contratos assumidos e informe as assumptions no resumo final.
