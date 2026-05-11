# Prompt — Fase 5 — Criar documentação

Você é um arquiteto de software sênior especialista em documentação técnica para times de engenharia.

Leia a spec:

```text
specs/bff-kotlin-scaffold/SPEC.md
```

Crie ou atualize a documentação do projeto.

## Criar ou atualizar

- `README.md`
- `docs/adr/0001-architecture-clean-lite-by-feature.md`
- `.github/copilot-instructions.md`
- arquivos em `.github/prompts`

## README deve conter

1. O que é o projeto.
2. Quando usar este scaffold.
3. Quando não usar este scaffold.
4. Arquitetura escolhida.
5. Estrutura de pastas.
6. Como rodar localmente.
7. Como rodar com Docker Compose.
8. Como executar e criar testes (deve explicar como executar o prompt correspondente : `generate-tests.prompt.md`).
9. Como criar uma nova feature (deve explicar como executar o prompt correspondente : `create-new-feature.prompt.md`).
10. Como criar um novo client externo (deve explicar como executar o prompt correspondente : `create-new-client.prompt.md`).
11. Como tratar erros.
12. Como configurar variáveis.
13. Como integrar autenticação real.
14. Checklist para Pull Request.
15. Exemplos de prompts para usar com GitHub Copilot.

## ADR deve explicar

- contexto;
- decisão;
- alternativas consideradas;
- por que não usar Hexagonal completa;
- por que não usar MVC solto;
- consequências positivas;
- riscos;
- como evoluir a arquitetura se o BFF crescer.

## Estilo

A documentação deve ser objetiva, clara e útil para desenvolvedor júnior.

Evite documentação genérica e superficial.

Use exemplos práticos.
