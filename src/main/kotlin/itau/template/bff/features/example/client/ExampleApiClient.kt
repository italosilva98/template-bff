package itau.template.bff.features.example.client

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker
import io.github.resilience4j.retry.annotation.Retry
import itau.template.bff.features.example.client.request.ExampleApiRequest
import itau.template.bff.features.example.client.response.ExampleApiResponse
import itau.template.bff.shared.config.RestClientConfig
import itau.template.bff.shared.error.ApiException
import itau.template.bff.shared.error.ExternalApiException
import itau.template.bff.shared.error.ResourceNotFoundException
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatusCode
import org.springframework.http.MediaType
import org.springframework.stereotype.Component

// Client responsável por encapsular a comunicação com a API externa de exemplo.
// Só esta classe conhece o contrato e os detalhes técnicos da API externa.
//
// @Retry e @CircuitBreaker aplicados apenas em operações idempotentes (GET, PUT).
// POST e DELETE não têm retry automático para evitar duplicação e deleções acidentais.
@Component
class ExampleApiClient(
    restClientConfig: RestClientConfig,
    properties: ExampleApiProperties
) {

    private val log = LoggerFactory.getLogger(ExampleApiClient::class.java)
    private val restClient = restClientConfig.buildClient(properties.baseUrl, properties.timeout)

    // ── GET ───────────────────────────────────────────────────────────────────

    @Retry(name = "example-api")
    @CircuitBreaker(name = "example-api")
    fun getById(id: String): ExampleApiResponse {
        log.debug("Calling example API GET /examples/{}", id)
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
            throw ex
        } catch (ex: Exception) {
            log.error("Unexpected error calling example API for id={}", id, ex)
            throw ExternalApiException("Failed to communicate with example API", cause = ex)
        }
    }

    // ── POST ──────────────────────────────────────────────────────────────────
    // Sem @Retry — POST não é idempotente; retry poderia criar duplicatas.

    @CircuitBreaker(name = "example-api")
    fun create(request: ExampleApiRequest): ExampleApiResponse {
        log.debug("Calling example API POST /examples")
        return try {
            restClient.post()
                .uri("/examples")
                .contentType(MediaType.APPLICATION_JSON)
                .body(request)
                .retrieve()
                .onStatus(HttpStatusCode::isError) { _, response ->
                    val status = response.statusCode.value()
                    log.warn("Example API returned error status={} on create", status)
                    throw ExternalApiException("External API error on create", externalStatus = status)
                }
                .body(ExampleApiResponse::class.java)
                ?: throw ExternalApiException("Empty response on create")
        } catch (ex: ApiException) {
            throw ex
        } catch (ex: Exception) {
            log.error("Unexpected error creating example", ex)
            throw ExternalApiException("Failed to communicate with example API", cause = ex)
        }
    }

    // ── PUT ───────────────────────────────────────────────────────────────────

    @Retry(name = "example-api")
    @CircuitBreaker(name = "example-api")
    fun update(id: String, request: ExampleApiRequest): ExampleApiResponse {
        log.debug("Calling example API PUT /examples/{}", id)
        return try {
            restClient.put()
                .uri("/examples/{id}", id)
                .contentType(MediaType.APPLICATION_JSON)
                .body(request)
                .retrieve()
                .onStatus(HttpStatusCode::isError) { _, response ->
                    val status = response.statusCode.value()
                    if (status == 404) throw ResourceNotFoundException("Example not found: $id")
                    log.warn("Example API returned error status={} on update id={}", status, id)
                    throw ExternalApiException("External API error on update", externalStatus = status)
                }
                .body(ExampleApiResponse::class.java)
                ?: throw ResourceNotFoundException("Example not found: $id")
        } catch (ex: ApiException) {
            throw ex
        } catch (ex: Exception) {
            log.error("Unexpected error updating example id={}", id, ex)
            throw ExternalApiException("Failed to communicate with example API", cause = ex)
        }
    }

    // ── DELETE ────────────────────────────────────────────────────────────────
    // Sem @Retry — DELETE poderia causar deleções duplicadas em caso de retry.

    @CircuitBreaker(name = "example-api")
    fun delete(id: String) {
        log.debug("Calling example API DELETE /examples/{}", id)
        try {
            restClient.delete()
                .uri("/examples/{id}", id)
                .retrieve()
                .onStatus(HttpStatusCode::isError) { _, response ->
                    val status = response.statusCode.value()
                    if (status == 404) throw ResourceNotFoundException("Example not found: $id")
                    log.warn("Example API returned error status={} on delete id={}", status, id)
                    throw ExternalApiException("External API error on delete", externalStatus = status)
                }
                .toBodilessEntity()
        } catch (ex: ApiException) {
            throw ex
        } catch (ex: Exception) {
            log.error("Unexpected error deleting example id={}", id, ex)
            throw ExternalApiException("Failed to communicate with example API", cause = ex)
        }
    }
}

