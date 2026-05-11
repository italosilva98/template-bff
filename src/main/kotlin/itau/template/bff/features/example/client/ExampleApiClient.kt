package itau.template.bff.features.example.client

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker
import io.github.resilience4j.retry.annotation.Retry
import itau.template.bff.features.example.client.response.ExampleApiResponse
import itau.template.bff.shared.config.RestClientConfig
import itau.template.bff.shared.error.ApiException
import itau.template.bff.shared.error.ExternalApiException
import itau.template.bff.shared.error.ResourceNotFoundException
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatusCode
import org.springframework.stereotype.Component

// Client responsável por encapsular a comunicação com a API externa de exemplo.
// Só esta classe conhece o contrato e os detalhes técnicos da API externa.
//
// @Retry — tenta até 3 vezes em caso de ExternalApiException (5xx, timeout, falha de rede).
//          Ignora ResourceNotFoundException (404 não deve ser retentado).
// @CircuitBreaker — abre o circuito após 50% de falhas em 10 chamadas.
//                   Quando aberto, lança CallNotPermittedException → 503 pelo GlobalExceptionHandler.
@Component
class ExampleApiClient(
    restClientConfig: RestClientConfig,
    properties: ExampleApiProperties
) {

    private val log = LoggerFactory.getLogger(ExampleApiClient::class.java)
    private val restClient = restClientConfig.buildClient(properties.baseUrl, properties.timeout)

    @Retry(name = "example-api")
    @CircuitBreaker(name = "example-api")
    fun getById(id: String): ExampleApiResponse {
        log.debug("Calling example API for id={}", id)
        return try {
            restClient.get()
                .uri("/examples/{id}", id)
                .retrieve()
                .onStatus(HttpStatusCode::isError) { _, response ->
                    val status = response.statusCode.value()
                    if (status == 404) throw ResourceNotFoundException("Example not found: $id")
                    log.warn("Example API returned error status={} for id={}", status, id)
                    throw ExternalApiException("External API error", externalStatus = status)
                }
                .body(ExampleApiResponse::class.java)
                ?: throw ResourceNotFoundException("Example not found: $id")
        } catch (ex: ApiException) {
            throw ex  // ResourceNotFoundException e ExternalApiException passam direto
        } catch (ex: Exception) {
            log.error("Unexpected error calling example API for id={}", id, ex)
            throw ExternalApiException("Failed to communicate with example API", cause = ex)
        }
    }
}
