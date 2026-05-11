# Arquitetura — Clean-Lite por Feature

## Objetivo

A arquitetura Clean-Lite por Feature busca equilibrar simplicidade, testabilidade e organização para BFFs em Kotlin.

Ela não é uma Clean Architecture completa.  
Ela não é uma Hexagonal Architecture completa.  
Ela é uma arquitetura pragmática para BFF.

## Fluxo padrão

```text
HTTP Request
  -> Controller
  -> UseCase
  -> Client externo
  -> API externa
```

## Responsabilidades

### Controller

Responsável por:

- receber request HTTP;
- validar entrada básica;
- delegar para o use case;
- retornar response HTTP.

Não deve:

- chamar client externo diretamente;
- fazer regra de negócio;
- fazer mapping complexo;
- tratar erro manualmente sem necessidade.

### UseCase

Responsável por:

- representar um caso de uso claro;
- orquestrar uma funcionalidade;
- chamar clients;
- aplicar regra leve de composição;
- chamar mapper;
- retornar resposta orientada ao BFF.

Nomes recomendados:

```text
GetCustomerSummaryUseCase
SearchProductsUseCase
CreateOrderDraftUseCase
ListAvailableOptionsUseCase
```

### Client

Responsável por:

- chamar APIs externas;
- conhecer base URL, headers, path e contrato externo;
- lidar com erros técnicos de integração;
- retornar DTOs externos.

Não deve:

- conhecer controller;
- retornar response pública do BFF;
- aplicar regra de apresentação.

### Mapper

Responsável por:

- converter DTO externo em modelo interno;
- converter modelo interno em response do BFF;
- isolar transformação de dados.

### Model

Responsável por representar modelos internos simples do BFF.

Evitar chamar de `domain` se não existir domínio real.

### Shared

Contém recursos transversais:

```text
config/
error/
observability/
security/
documentation/
resilience/
```

Regra importante: `shared` não deve depender de uma feature específica.

## Estrutura sugerida

```text
features/
  customer/
    api/
      CustomerController.kt
      request/
      response/
    application/
      GetCustomerSummaryUseCase.kt
    client/
      CustomerApiClient.kt
      CustomerApiProperties.kt
      response/
    mapper/
      CustomerMapper.kt
    model/
      CustomerView.kt
```

## Regras

1. Controller nunca chama client diretamente.
2. UseCase coordena o fluxo.
3. Client conhece detalhes externos.
4. DTO externo não vaza para API pública.
5. Mapper transforma dados.
6. Não criar interface sem necessidade.
7. Não criar repository sem banco.
8. Não criar base classes genéricas sem prova de necessidade.
9. Testes devem validar comportamento.
10. Documentação deve ensinar pelo exemplo.

## Anti-patterns

Evitar:

```text
BaseController
BaseUseCase
BaseClient
GenericApiResponse<T>
AbstractRestClient
CommonService
Utils gigantes
Services com centenas de linhas
DTO externo retornado no controller
Client chamado diretamente no controller
```

## Exemplo conceitual

```kotlin
@RestController
@RequestMapping("/api/v1/customers")
class CustomerController(
    private val getCustomerSummaryUseCase: GetCustomerSummaryUseCase
) {
    @GetMapping("/{id}/summary")
    fun getSummary(@PathVariable id: String): ResponseEntity<CustomerSummaryResponse> {
        return ResponseEntity.ok(getCustomerSummaryUseCase.execute(id))
    }
}
```

```kotlin
@Service
class GetCustomerSummaryUseCase(
    private val customerApiClient: CustomerApiClient,
    private val orderApiClient: OrderApiClient,
    private val customerMapper: CustomerMapper
) {
    fun execute(id: String): CustomerSummaryResponse {
        val customer = customerApiClient.getById(id)
        val orders = orderApiClient.getOrdersByCustomerId(id)

        val view = customerMapper.toView(customer, orders)

        return customerMapper.toResponse(view)
    }
}
```
