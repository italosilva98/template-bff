# Prompt — Corrigir achados da revisão

Você é um desenvolvedor sênior Kotlin/Spring Boot.

Use a revisão arquitetural feita anteriormente e corrija os problemas encontrados.

## Regras

- Corrigir primeiro problemas críticos e altos.
- Não reescrever o projeto inteiro sem necessidade.
- Preservar a arquitetura Clean-Lite por Feature.
- Não adicionar complexidade desnecessária.
- Não criar novas abstrações sem necessidade.
- Manter testes funcionando.
- Atualizar documentação quando a mudança alterar uso ou arquitetura.

## Entregue

Para cada correção:

1. Arquivo alterado.
2. O que foi corrigido.
3. Por que foi corrigido.
4. Como validar.

## Validação final

Execute ou indique:

```bash
./gradlew clean build
./gradlew test
docker compose up --build
```
