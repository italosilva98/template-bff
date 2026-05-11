# ADR-0001 — Usar Clean-Lite por Feature para BFF Kotlin

**Status:** Aceito  
**Data:** 2026-05-10  
**Contexto:** Scaffold de BFF Kotlin com Spring Boot  
**Decisores:** Time de Plataforma

## Contexto

O time deseja criar um scaffold/template de BFF em Kotlin para acelerar a criação de novos backends-for-frontends.

As aplicações BFF normalmente possuem responsabilidades como:

- expor contratos específicos para o front-end;
- orquestrar chamadas a APIs internas;
- adaptar respostas externas para modelos orientados à tela;
- centralizar tratamento de erro;
- padronizar logs, correlation id e observabilidade;
- propagar autenticação/autorização;
- reduzir complexidade do front-end.

Por outro lado, BFFs geralmente não são os donos principais do domínio de negócio. Por isso, uma arquitetura pesada demais pode gerar mais custo do que benefício.

## Decisão

Adotar uma arquitetura **Clean-Lite por Feature**.

Fluxo principal:

```text
Controller -> UseCase -> Client -> External API
```

Organização por feature:

```text
features/
  example/
    api/
    application/
    client/
    mapper/
    model/
```

Recursos compartilhados:

```text
shared/
  config/
  error/
  observability/
  security/
  documentation/
  resilience/
```

## Alternativas consideradas

### MVC simples

Estrutura típica:

```text
controller/
service/
client/
dto/
```

Vantagens:

- muito simples;
- fácil para devs júnior;
- baixa cerimônia.

Problemas:

- risco de `Service` virar uma classe gigante;
- menor clareza sobre casos de uso;
- organização tende a piorar conforme o BFF cresce;
- mistura fácil de regra de orquestração, mapping e integração.

### Clean Architecture completa

Estrutura típica:

```text
domain/
application/
ports/
adapters/
infrastructure/
```

Vantagens:

- excelente isolamento;
- boa testabilidade;
- boa para domínios ricos.

Problemas:

- pesada para muitos BFFs;
- cria muitas interfaces e camadas;
- aumenta custo cognitivo;
- pode virar overengineering.

### Hexagonal Architecture completa

Estrutura típica:

```text
ports/in
ports/out
adapters/in
adapters/out
application
domain
```

Vantagens:

- protege bem o domínio;
- isola entradas e saídas;
- boa para sistemas com regras próprias relevantes.

Problemas:

- BFF geralmente já é uma camada de borda;
- criar ports/adapters para tudo pode ser excessivo;
- dev júnior pode ter mais dificuldade para evoluir.

## Consequências positivas

- Estrutura simples e previsível.
- Fácil criação de novas features.
- Menor risco de overengineering.
- Melhor organização que MVC puro.
- Testabilidade suficiente para BFF.
- Bom equilíbrio entre produtividade e manutenibilidade.
- Dev júnior consegue seguir exemplos.

## Riscos

- Se o BFF crescer demais, a camada `application` pode acumular lógica excessiva.
- Se muitas integrações forem adicionadas, os clients podem precisar de maior padronização.
- Se regras de negócio reais surgirem no BFF, a ausência de uma camada de domínio pode limitar a clareza.

## Mitigações

- Manter use cases pequenos e orientados a ação.
- Criar mappers explícitos.
- Não retornar DTO externo diretamente.
- Separar contratos públicos de contratos externos.
- Criar ADRs adicionais caso a arquitetura precise evoluir.
- Só introduzir `domain`, `ports` e `adapters` quando houver necessidade real.

## Quando evoluir a arquitetura

Evoluir para uma arquitetura mais robusta caso ocorram sinais como:

- use cases com muita regra de negócio;
- múltiplas implementações para a mesma integração;
- necessidade forte de isolamento de domínio;
- regras reutilizadas em diferentes entradas;
- aumento significativo de complexidade;
- BFF deixando de ser apenas camada de composição.

Possível evolução futura:

```text
features/
  customer/
    api/
    application/
    domain/
    ports/
    adapters/
```

Essa evolução não deve ser feita preventivamente.

---

## Motivação desta ADR

Esta ADR existe para que qualquer desenvolvedor que entre no time entenda **por que** a arquitetura foi escolhida assim — não apenas o que foi decidido.

BFFs têm características diferentes de um serviço de domínio:

- São **pontos de composição**, não donos de negócio
- Têm vida útil ligada ao cliente (front-end, mobile app)
- Precisam evoluir rápido conforme a UI muda
- São mantidos por times às vezes pequenos
- Raramente precisam de múltiplos repositórios, eventos ou domínio rico

Por isso, a arquitetura deve ser proporcional à responsabilidade.

### Por que não usar Hexagonal completa

Hexagonal cria duas fronteiras explícitas: portas de entrada (use cases) e portas de saída (repositórios/adapters), com interfaces para tudo.

Para um BFF, isso gera:

```text
CustomerPort (interface)
    ↑ implementado por
CustomerAdapter
    ↑ injetado em
GetCustomerUseCase
    ↑ atrás de
IGetCustomerUseCase (outra interface)
    ↑ chamado pelo
CustomerController
```

Custo: muitos arquivos, muita indireção, pouco benefício.

Em um BFF que nunca vai ter outra implementação de `CustomerPort`, a interface é cerimônia sem propósito.

**Regra prática:** só criar interface quando houver (ou houver previsão concreta de) múltiplas implementações.

### Por que não usar MVC solto

MVC puro (controller → service → repository) funciona bem no início, mas em BFFs com múltiplas integrações:

- O `Service` vira uma classe de 400 linhas misturando orquestração, mapping e chamadas HTTP
- Fica difícil testar partes isoladamente
- Features diferentes acabam acopladas no mesmo `Service`

Clean-Lite resolve isso sem adicionar camadas desnecessárias: cada feature tem seu próprio `UseCase` focado, seu próprio `Client` e seu próprio `Mapper`.

## Consequências positivas

- **Previsibilidade:** qualquer dev que conhece uma feature sabe onde encontrar as mesmas peças nas outras
- **Isolamento:** mudar a feature `orders` não afeta `customers`
- **Testabilidade:** cada camada pode ser testada de forma simples e isolada
- **Velocidade:** criar uma nova feature é copiar um padrão conhecido
- **Acessibilidade:** devs júnior conseguem contribuir seguindo o exemplo

## Riscos

| Risco | Probabilidade | Impacto | Mitigação |
|---|---|---|---|
| UseCase acumula lógica excessiva | Média | Alto | Manter use cases focados em um caso de uso; dividir se necessário |
| Mappers negligenciados | Alta | Médio | Não pular o mapper; jamais retornar DTO externo direto |
| Shared crescer sem controle | Baixa | Médio | Colocar em `shared/` apenas o que é genuinamente transversal |
| Feature virar monolito interno | Baixa | Alto | Sinal para evoluir arquitetura (ver seção abaixo) |

## Como evoluir a arquitetura se o BFF crescer

Esta arquitetura é um ponto de partida, não um teto.

**Sinal de que algo precisa mudar:**

- UseCase com mais de ~80 linhas de lógica de orquestração
- Lógica de negócio real aparecendo (cálculos, regras, validações complexas)
- Mesma regra sendo duplicada em múltiplas features
- Necessidade de múltiplas implementações para um mesmo client (ex: HTTP vs mock vs DB)

**Caminho de evolução:**

```text
# Passo 1 — adicionar model interno mais rico
features/
  customer/
    model/
      Customer.kt      ← entidade interna com comportamentos simples

# Passo 2 — introduzir ports somente onde há múltiplas implementações
features/
  customer/
    ports/
      CustomerRepository.kt   ← interface
    adapters/
      HttpCustomerAdapter.kt  ← implementação HTTP
      CacheCustomerAdapter.kt ← implementação cache (nova)

# Passo 3 — considerar extrair para serviço separado
# se o BFF deixou de ser composição e virou dono do domínio
```

A evolução deve ser **incremental e local** — não refatorar o projeto inteiro. Aplicar em uma feature quando o sinal aparecer nela.

Essa evolução não deve ser feita preventivamente.
