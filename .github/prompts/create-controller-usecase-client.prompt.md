# Prompt — Criar fluxo Controller -> UseCase -> Client

Você é um desenvolvedor sênior Kotlin/Spring Boot.

Crie um fluxo completo para uma nova rota de BFF seguindo a arquitetura Clean-Lite por Feature.

## Dados da implementação

Preencha antes:

```text
Feature:
Endpoint:
Método HTTP:
Path variables:
Query params:
Body:
API externa:
Response do BFF:
Erros esperados:
```

## Criar

- Controller
- Request DTO, se necessário
- Response DTO
- UseCase
- Client externo, se necessário
- Client DTOs, se necessário
- Mapper
- Model interno, se necessário
- Testes

## Fluxo obrigatório

```text
Controller -> UseCase -> Client -> Mapper -> Response
```

## Regras

- Controller não pode chamar client.
- UseCase não deve conhecer detalhes HTTP do controller.
- Client não deve retornar response pública do BFF.
- DTO externo não deve vazar para API pública.
- Mapper deve isolar conversões.
- Exceptions devem usar `shared/error`.
- Criar testes comportamentais.

## Resultado esperado

Código compilando, testado e seguindo os padrões do scaffold.
