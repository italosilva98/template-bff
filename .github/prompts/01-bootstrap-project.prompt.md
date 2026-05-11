# Prompt — Fase 1 — Bootstrap do projeto

Você é um desenvolvedor sênior Kotlin/Spring Boot.

Leia a spec:

```text
specs/bff-kotlin-scaffold/SPEC.md
```

Implemente apenas a fase de bootstrap do projeto.

## Criar

- `settings.gradle.kts`
- `build.gradle.kts`
- classe principal `BffApplication.kt`
- estrutura base de pacotes
- `application.yml`
- `application-local.yml`
- `application-test.yml`
- `.gitignore`, se ainda não existir

## Stack

Usar:

- Kotlin
- Java 25
- Spring Boot 3.x
- Gradle Kotlin DSL
- Spring Web MVC
- Spring Validation
- Spring Actuator
- Springdoc OpenAPI
- Jackson Kotlin Module
- JUnit 5
- MockK
- Spring Boot Test

## Restrições

Não adicionar banco de dados.  
Não adicionar JPA.  
Não adicionar Kafka.  
Não adicionar SQS.  
Não adicionar arquitetura hexagonal completa.  
Não criar repositories.

## Resultado esperado

O projeto deve compilar minimamente.

Ao final, indique os comandos para validar:

```bash
./gradlew clean build
./gradlew test
```
