# Prompt — Gerar testes para uma feature existente

Você é um engenheiro especialista em testes para Kotlin/Spring Boot.

Gere ou melhore os testes de uma feature existente.

## Feature

Preencha:

```text
Nome da feature:
Classes principais:
Cenários críticos:
```

## Testes esperados

Criar ou revisar:

- teste de controller;
- teste de use case;
- teste de mapper;
- teste de client, se houver integração externa.

## Boas práticas

- Testar comportamento, não detalhe irrelevante.
- Usar nomes claros nos testes.
- Evitar mocks excessivos.
- Evitar testes frágeis.
- Não depender de API real.
- Validar erro e fluxo feliz.
- Validar contratos importantes do response.

## Resultado esperado

Todos os testes devem rodar com:

```bash
./gradlew test
```
