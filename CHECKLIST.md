# Checklist de aceite — BFF Kotlin Scaffold

Use este checklist após o Copilot gerar o projeto.

## Build e execução

- [ ] O projeto compila com `./gradlew clean build`
- [ ] Os testes rodam com `./gradlew test`
- [ ] O app sobe localmente
- [ ] O app sobe com `docker compose up --build`
- [ ] A porta padrão é `8080`
- [ ] O profile local funciona corretamente

## Endpoints mínimos

- [ ] `/actuator/health` responde corretamente
- [ ] `/actuator/info` responde corretamente
- [ ] Swagger/OpenAPI está acessível
- [ ] `GET /api/v1/examples/{id}` funciona no fluxo feliz
- [ ] `GET /api/v1/examples/{id}` retorna erro padronizado quando o recurso não existe
- [ ] O mock de API externa funciona no Docker Compose

## Arquitetura

- [ ] A arquitetura é Clean-Lite por Feature
- [ ] Existe pasta `features/example`
- [ ] Existe pasta `shared`
- [ ] Controller não chama client diretamente
- [ ] UseCase orquestra o fluxo
- [ ] Client conhece detalhes da API externa
- [ ] Mapper converte DTO externo para modelo/resposta do BFF
- [ ] DTO externo não vaza para o contrato público
- [ ] Não existem repositories
- [ ] Não existe banco de dados
- [ ] Não existe Hexagonal Architecture completa
- [ ] Não existe Clean Architecture purista
- [ ] Não existem interfaces desnecessárias
- [ ] Não existem classes genéricas como `BaseController`, `BaseUseCase` ou `BaseClient`

## Tratamento de erro

- [ ] Existe `GlobalExceptionHandler`
- [ ] Existe `ErrorResponse`
- [ ] Erros de validação são tratados
- [ ] Erros de recurso não encontrado são tratados
- [ ] Erros de API externa são tratados
- [ ] Erros inesperados não expõem stacktrace
- [ ] Respostas de erro incluem `correlationId`
- [ ] Respostas de erro incluem `timestamp`, `status`, `error`, `message` e `path`

## Observabilidade

- [ ] O header `X-Correlation-Id` é propagado quando recebido
- [ ] Um correlation id é gerado quando o header não vem na request
- [ ] Logs incluem correlation id
- [ ] Não há log de token ou dado sensível
- [ ] Actuator está configurado de forma mínima e segura

## Segurança

- [ ] Não há secrets hardcoded
- [ ] A segurança local é simples e documentada
- [ ] Está documentado onde integrar OAuth2/JWT futuramente
- [ ] Não há autenticação real acoplada a provedor específico sem necessidade

## Testes

- [ ] Existe teste de controller
- [ ] Existe teste de use case
- [ ] Existe teste de mapper
- [ ] Existe teste de client externo
- [ ] Testes validam comportamento, não apenas implementação
- [ ] Existe teste para erro de integração externa
- [ ] Existe teste para recurso não encontrado

## Documentação

- [ ] README explica o objetivo do projeto
- [ ] README explica quando usar e quando não usar
- [ ] README explica a arquitetura
- [ ] README ensina criar nova feature
- [ ] README ensina criar novo client externo
- [ ] README explica tratamento de erros
- [ ] README explica execução local
- [ ] README explica execução via Docker Compose
- [ ] Existe ADR da arquitetura
- [ ] Existe `.github/copilot-instructions.md`
- [ ] Existem prompts em `.github/prompts`
