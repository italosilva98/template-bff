# Prompt — Fase 2 — Shared Foundation

Você é um arquiteto de software especialista em BFFs Kotlin.

Leia a spec:

```text
specs/bff-kotlin-scaffold/SPEC.md
```

Implemente apenas a base compartilhada do projeto.

## Criar pacotes

```text
shared/config
shared/error
shared/observability
shared/documentation
shared/security
shared/resilience
```

## Criar recursos mínimos

### Config

- `RestClientConfig.kt`
- configuração simples para `RestClient`
- suporte a timeout
- suporte a base URL por properties quando necessário

### Error

- `ApiException.kt`
- `ExternalApiException.kt`
- `ResourceNotFoundException.kt`
- `ErrorResponse.kt`
- `GlobalExceptionHandler.kt`

O erro deve ter:

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

### Observability

- `CorrelationIdFilter.kt`
- suporte ao header `X-Correlation-Id`
- geração de correlation id quando ausente
- inclusão no MDC para logs

### Documentation

- `OpenApiConfig.kt`

### Security

- `AuthenticatedUser.kt`
- `CurrentUserProvider.kt`
- `SecurityConfig.kt`

A segurança deve permitir ambiente local sem autenticação real.

### Resilience

- configuração simples para timeout/retry/circuit breaker se fizer sentido
- não aplicar retry automaticamente em tudo

## Restrições

Não criar framework interno.  
Não criar abstrações genéricas complexas.  
Não criar classes base genéricas.  
Não acoplar segurança a Cognito, Keycloak ou Azure AD neste momento.  
Não logar dados sensíveis.

## Resultado esperado

A base compartilhada deve ser simples, funcional e documentada no código quando necessário.
