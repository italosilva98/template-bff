# Prompt — Criar nova feature no BFF

Você é um desenvolvedor sênior Kotlin/Spring Boot.

Crie uma nova feature seguindo a arquitetura Clean-Lite por Feature deste projeto.

## Informações da feature

Preencha antes de executar:

```text
Nome da feature:
Endpoint:
Método HTTP:
Request esperado:
Response esperado:
APIs externas chamadas:
Regras de composição:
Cenários de erro:
```

## Estrutura obrigatória

Criar em:

```text
src/main/kotlin/br/com/company/bff/features/{feature-name}
```

Com:

```text
api/
application/
client/
mapper/
model/
```

Use apenas as pastas necessárias, mas siga o padrão.

## Regras

- Controller não chama client diretamente.
- Controller delega para UseCase.
- UseCase orquestra fluxo.
- Client externo encapsula chamada HTTP.
- DTO externo não vaza para API pública.
- Mapper transforma dados.
- Exceptions devem usar padrão de `shared/error`.
- Correlation id deve ser preservado.
- Criar testes junto com a feature.

## Testes

Criar testes para:

- Controller
- UseCase
- Mapper
- Client, se houver integração externa

## Restrições

Não criar repository sem banco.  
Não criar interface sem necessidade.  
Não criar classe base genérica.  
Não criar camada domain rica sem necessidade.  
Não alterar arquitetura do projeto.

## Resultado esperado

A nova feature deve compilar, ter testes e seguir o padrão da feature `example`.
