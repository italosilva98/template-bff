# Prompt — Gerar projeto completo a partir da spec

Você é um arquiteto de software sênior especialista em Kotlin, Spring Boot e criação de scaffolds corporativos.

Leia integralmente o arquivo:

```text
specs/bff-kotlin-scaffold/SPEC.md
```

Implemente o projeto exatamente conforme a spec.

## Prioridades

1. Simplicidade.
2. Clareza para desenvolvedor júnior.
3. Arquitetura Clean-Lite por Feature.
4. Código funcional.
5. Testes úteis.
6. Documentação completa.
7. Nada de overengineering.

## Restrições

Não crie arquitetura hexagonal completa.  
Não crie Clean Architecture purista.  
Não adicione banco de dados.  
Não adicione repositories.  
Não crie interfaces desnecessárias.  
Não crie classes base genéricas.  
Não crie framework interno.

## Fases de implementação

Implemente em fases:

1. Bootstrap do projeto.
2. Shared foundation.
3. Feature example.
4. Testes.
5. Docker e docker-compose.
6. README, ADR e arquivos de prompt para Copilot.
7. Validação final.

## Critérios finais

Ao final, o projeto deve:

- compilar;
- rodar testes;
- subir localmente;
- subir via Docker Compose;
- expor health check;
- expor Swagger/OpenAPI;
- ter a feature example funcionando;
- ter erro padronizado;
- ter correlation id;
- ter documentação suficiente para dev júnior.

## Validação

Execute ou indique os comandos:

```bash
./gradlew clean build
./gradlew test
docker compose up --build
```

Se algum requisito não puder ser implementado, explique o motivo e proponha a alternativa mais simples.
