plugins {
	kotlin("jvm") version "2.2.21"
	kotlin("plugin.spring") version "2.2.21"
	id("org.springframework.boot") version "4.0.6"
	id("io.spring.dependency-management") version "1.1.7"
}

group = "itau.template"
version = "0.0.1-SNAPSHOT"

repositories {
	mavenCentral()
}

dependencies {
	// Spring Boot Starters
	implementation("org.springframework.boot:spring-boot-starter-webmvc")
	implementation("org.springframework.boot:spring-boot-starter-actuator")
	implementation("org.springframework.boot:spring-boot-starter-validation")
	implementation("org.springframework.boot:spring-boot-starter-security")

	// Kotlin
	implementation("org.jetbrains.kotlin:kotlin-reflect")

	// Jackson Kotlin Module (Jackson 3.x coordinates, usados pelo Spring Boot 4)
	implementation("tools.jackson.module:jackson-module-kotlin")

	// OpenAPI / Swagger UI
	implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:3.0.2")

	// Resilience4j — retry e circuit breaker com auto-configuração Spring Boot
	// Inclui resilience4j-spring (AOP) e as anotações @Retry / @CircuitBreaker
	implementation("io.github.resilience4j:resilience4j-spring-boot3:2.3.0")

	// AspectJ — necessário para o proxy AOP que processa @Retry e @CircuitBreaker
	implementation("org.aspectj:aspectjweaver")

	// Structured logging (JSON) — usado no perfil prod via logback-spring.xml
	runtimeOnly("net.logstash.logback:logstash-logback-encoder:8.0")

	// Tests
	testImplementation("org.springframework.boot:spring-boot-starter-webmvc-test")
	testImplementation("org.springframework.boot:spring-boot-starter-security-test")
	testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
	testImplementation("io.mockk:mockk:1.13.12")
	testImplementation("org.wiremock:wiremock-standalone:3.9.2")
	testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

kotlin {
	compilerOptions {
		jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_24)
		freeCompilerArgs.addAll("-Xjsr305=strict", "-Xannotation-default-target=param-property")
	}
}

tasks.withType<JavaCompile> {
	options.release.set(24)
}

tasks.withType<Test> {
	useJUnitPlatform()
}
