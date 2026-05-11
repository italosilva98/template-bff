# Prompt — Fase 4 — Gerar testes

Você é um engenheiro de software especialista em testes para Kotlin e Spring Boot.

Leia a spec:

```text
specs/bff-kotlin-scaffold/SPEC.md
```

Crie testes úteis para o scaffold.

## Testes obrigatórios

Criar testes para:

- `ExampleController`
- `GetExampleUseCase`
- `ExampleMapper`
- `ExampleApiClient`
- `GlobalExceptionHandler`, se fizer sentido

## Validar cenários

- fluxo feliz;
- recurso não encontrado;
- erro de API externa;
- contrato básico do controller;
- mapping entre DTO externo e response do BFF;
- presença de correlation id em erro, se aplicável.

## Ferramentas

Usar:

- JUnit 5
- MockK
- Spring Boot Test
- MockMvc para controller, se apropriado
- WireMock ou MockWebServer para client HTTP

## Regras

- Não criar testes frágeis.
- Não testar detalhes irrelevantes de implementação.
- Não usar sleeps.
- Não depender de API externa real.
- Não aumentar complexidade do projeto sem necessidade.

## Resultado esperado

Os testes devem rodar com:

```bash
./gradlew test
```
