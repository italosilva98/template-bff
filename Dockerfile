# ────────────────────────────────────────────────────────────────────────────────
# Estágio 1 — Build
# Usa Gradle wrapper para compilar e gerar o fat JAR.
# O cache das dependências Gradle é preservado entre builds pelo layer cache do Docker.
# ────────────────────────────────────────────────────────────────────────────────
FROM eclipse-temurin:25-jdk-alpine AS build

WORKDIR /app

# Copia apenas os arquivos do Gradle wrapper primeiro para aproveitar cache de layers
COPY gradlew .
COPY gradle/ gradle/
COPY build.gradle.kts .
COPY settings.gradle.kts .

# Baixa dependências antes de copiar o código (melhora cache)
RUN ./gradlew dependencies --no-daemon -q || true

# Copia o código-fonte e gera o JAR
COPY src/ src/
RUN ./gradlew bootJar --no-daemon -x test

# ────────────────────────────────────────────────────────────────────────────────
# Estágio 2 — Runtime
# Usa apenas JRE (menor e mais seguro que JDK).
# ────────────────────────────────────────────────────────────────────────────────
FROM eclipse-temurin:25-jre-alpine AS runtime

WORKDIR /app

# Cria usuário não-root para rodar a aplicação (boa prática de segurança)
RUN addgroup -S appgroup && adduser -S appuser -G appgroup

# Copia apenas o JAR do estágio de build
COPY --from=build /app/build/libs/*.jar app.jar

USER appuser

EXPOSE 8080

# Opções JVM recomendadas para containers:
#   -XX:+UseContainerSupport  → respeita os limites de CPU/memória do container
#   -XX:MaxRAMPercentage      → usa até 75% da RAM disponível para o heap
ENTRYPOINT ["java", \
  "-XX:+UseContainerSupport", \
  "-XX:MaxRAMPercentage=75.0", \
  "-jar", "app.jar"]
