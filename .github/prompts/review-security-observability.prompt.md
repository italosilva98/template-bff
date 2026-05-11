# Prompt — Revisar segurança e observabilidade

Você é um arquiteto de software especialista em segurança, observabilidade e BFFs em Kotlin/Spring Boot.

Revise o projeto com foco em segurança e observabilidade.

## Segurança

Verifique:

- se existe secret hardcoded;
- se tokens são logados;
- se dados sensíveis são logados;
- se stacktrace é exposto em response;
- se headers sensíveis são propagados indevidamente;
- se CORS está permissivo sem justificativa;
- se Actuator expõe endpoints demais;
- se o modo local sem autenticação está documentado;
- se há ponto claro para integração futura com OAuth2/JWT.

## Observabilidade

Verifique:

- se toda request possui correlation id;
- se o header `X-Correlation-Id` é propagado;
- se logs incluem correlation id;
- se erros retornam correlation id;
- se logs são úteis e não excessivos;
- se chamadas externas possuem logs/erros rastreáveis;
- se health check está disponível;
- se métricas básicas estão disponíveis via Actuator.

## Entregue

Para cada problema:

- severidade;
- risco;
- evidência no código;
- sugestão de correção;
- arquivos afetados.

Não faça alterações automaticamente sem explicar.
