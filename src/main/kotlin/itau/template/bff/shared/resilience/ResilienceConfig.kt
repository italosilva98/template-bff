package itau.template.bff.shared.resilience

import io.github.resilience4j.circuitbreaker.CircuitBreaker
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry
import io.github.resilience4j.retry.Retry
import io.github.resilience4j.retry.RetryRegistry
import jakarta.annotation.PostConstruct
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Configuration

// Registra listeners de observabilidade para todas as instâncias Resilience4j.
//
// As instâncias (retry, circuit breaker) são definidas em application.yml e ativadas
// pelas anotações @Retry / @CircuitBreaker nos clients externos.
//
// Esta classe não cria instâncias — apenas observa eventos delas para fins de log.
// Para adicionar novas instâncias, criar entradas em application.yml e anotar o client.
@Configuration
class ResilienceConfig(
    private val circuitBreakerRegistry: CircuitBreakerRegistry,
    private val retryRegistry: RetryRegistry
) {

    private val log = LoggerFactory.getLogger(ResilienceConfig::class.java)

    @PostConstruct
    fun registerEventListeners() {
        // Instâncias criadas antes do @PostConstruct (ex: pre-inicializadas pelo health indicator)
        circuitBreakerRegistry.allCircuitBreakers.forEach(::attachCircuitBreakerListeners)
        retryRegistry.allRetries.forEach(::attachRetryListeners)

        // Instâncias criadas depois (lazy, na primeira chamada anotada com @CircuitBreaker)
        circuitBreakerRegistry.eventPublisher.onEntryAdded { event ->
            attachCircuitBreakerListeners(event.addedEntry)
        }
        retryRegistry.eventPublisher.onEntryAdded { event ->
            attachRetryListeners(event.addedEntry)
        }
    }

    private fun attachCircuitBreakerListeners(cb: CircuitBreaker) {
        // Loga toda mudança de estado: CLOSED → OPEN → HALF_OPEN → CLOSED
        cb.eventPublisher.onStateTransition { event ->
            log.warn(
                "CircuitBreaker [{}]: {} → {}",
                event.circuitBreakerName,
                event.stateTransition.fromState,
                event.stateTransition.toState
            )
        }
        // Loga falhas individuais registradas pelo circuit breaker (nível debug para não poluir)
        cb.eventPublisher.onError { event ->
            log.debug(
                "CircuitBreaker [{}] falha registrada: {}",
                event.circuitBreakerName,
                event.throwable.message
            )
        }
    }

    private fun attachRetryListeners(retry: Retry) {
        // Loga cada tentativa de retry com número da tentativa e motivo
        retry.eventPublisher.onRetry { event ->
            log.warn(
                "Retry [{}] tentativa #{} — último erro: {}",
                event.name,
                event.numberOfRetryAttempts,
                event.lastThrowable?.message
            )
        }
        // Loga quando todas as tentativas se esgotam sem sucesso
        retry.eventPublisher.onError { event ->
            log.error(
                "Retry [{}] esgotado após {} tentativas — último erro: {}",
                event.name,
                event.numberOfRetryAttempts,
                event.lastThrowable?.message
            )
        }
    }
}
