# Prompt — Revisão arquitetural do BFF

Você é um arquiteto staff/principal especialista em Kotlin, Spring Boot, BFFs e plataformas internas de desenvolvimento.

Revise este projeto gerado a partir da spec:

```text
specs/bff-kotlin-scaffold/SPEC.md
```

Faça uma análise crítica e objetiva.

## Verifique

1. Se a arquitetura Clean-Lite por Feature foi respeitada.
2. Se há overengineering.
3. Se existe acoplamento indevido.
4. Se controllers chamam clients diretamente.
5. Se DTOs externos vazam para a API pública.
6. Se os use cases estão com responsabilidade correta.
7. Se o tratamento de erro está adequado.
8. Se correlation id e logs estão bem implementados.
9. Se a documentação é suficiente para dev júnior.
10. Se os testes realmente validam comportamento útil.
11. Se há problemas de segurança.
12. Se há dependências desnecessárias.
13. Se o Dockerfile e o docker-compose estão coerentes.
14. Se a configuração local/test/prod está bem separada.
15. Se o código é idiomático e legível.
16. Se o projeto é fácil de entender e evoluir.
17. Se o README é claro, completo e útil.
18. Se os prompts são claros, completos e úteis.
19. Se as ADRs explicam bem as decisões arquiteturais.
20. Se o projeto segue as convenções estabelecidas.
21. Se o projeto é consistente com as melhores práticas de BFFs, Kotlin e Spring Boot, arquitetura de software, design patterns e SOLID.

## Não faça

Não reescreva o projeto inteiro.

## Entregue

Para cada problema encontrado, informe:

- problema;
- severidade: baixa, média, alta ou crítica;
- justificativa;
- sugestão de correção;
- arquivos que devem ser alterados;
- ordem recomendada de ajuste.

## Critério

Se algo estiver bom, diga objetivamente que está bom.

Se houver overengineering, seja direto e peça simplificação.
