# Prompt — Fase 3 — Criar feature de exemplo

Você é um desenvolvedor sênior Kotlin/Spring Boot.

Leia a spec:

```text
specs/bff-kotlin-scaffold/SPEC.md
```

Crie a feature `example` completa seguindo a arquitetura Clean-Lite por Feature.

## Estrutura esperada

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

## Endpoint

Criar:

```http
GET /api/v1/examples/{id}
```

## Fluxo

O fluxo deve ser:

```text
ExampleController
  -> GetExampleUseCase
  -> ExampleApiClient
  -> ExampleMapper
  -> ExampleResponse
```

## Regras

- Controller não chama client diretamente.
- UseCase orquestra o fluxo.
- Client usa `RestClient`.
- Client usa base URL configurada por properties.
- Mapper converte resposta externa para modelo interno e response do BFF.
- DTO externo não deve ser retornado pelo controller.
- Tratamento de erro deve usar as exceptions compartilhadas.

## Mock externo

Preparar o client para chamar uma API externa mockada em ambiente local.

A URL local deve poder ser configurada em `application-local.yml`.

## Resultado esperado

A rota `GET /api/v1/examples/{id}` deve funcionar em ambiente local.
