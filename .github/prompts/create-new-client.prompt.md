# Prompt — Criar novo client externo

Você é um desenvolvedor sênior Kotlin/Spring Boot especialista em integrações HTTP.

Crie um novo client externo seguindo o padrão deste BFF.

## Informações do client

Preencha antes de executar:

```text
Nome da API externa:
Base URL property:
Endpoints chamados:
Métodos HTTP:
Headers necessários:
Request DTO:
Response DTO:
Timeout:
Retry necessário? Sim/Não. Justifique:
Erros conhecidos:
```

## Estrutura esperada

Criar dentro da feature correspondente:

```text
features/{feature}/client/{ExternalApiClient}.kt
features/{feature}/client/{ExternalApiProperties}.kt
features/{feature}/client/request/
features/{feature}/client/response/
```

## Regras

- Usar `RestClient`.
- Base URL deve vir de `application.yml`.
- Timeout deve ser configurável.
- Não hardcodar URL.
- Não hardcodar secrets.
- Não logar token.
- Não retornar DTO externo diretamente pelo controller.
- Converter erros externos em exceptions do pacote `shared/error`.
- Propagar correlation id quando aplicável.
- Usar retry somente em chamadas idempotentes e quando justificado.

## Testes

Criar teste do client com WireMock ou MockWebServer.

Validar:

- fluxo feliz;
- 404 externo;
- 5xx externo;
- timeout, se aplicável;
- headers importantes.

## Resultado esperado

O client deve ser simples, explícito e fácil de usar pelo UseCase.
