package itau.template.bff.shared.error

import itau.template.bff.shared.observability.CorrelationIdFilter
import io.github.resilience4j.circuitbreaker.CallNotPermittedException
import jakarta.servlet.http.HttpServletRequest
import org.slf4j.LoggerFactory
import org.slf4j.MDC
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import java.time.LocalDateTime

@RestControllerAdvice
class GlobalExceptionHandler {

    private val log = LoggerFactory.getLogger(GlobalExceptionHandler::class.java)

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleValidation(
        ex: MethodArgumentNotValidException,
        request: HttpServletRequest
    ): ResponseEntity<ErrorResponse> {
        val message = ex.bindingResult.fieldErrors
            .joinToString(", ") { "${it.field}: ${it.defaultMessage}" }

        log.warn("Validation error on [{}]: {}", request.requestURI, message)

        return ResponseEntity.badRequest()
            .body(buildError(400, "VALIDATION_ERROR", message, request))
    }

    @ExceptionHandler(ResourceNotFoundException::class)
    fun handleNotFound(
        ex: ResourceNotFoundException,
        request: HttpServletRequest
    ): ResponseEntity<ErrorResponse> {
        log.warn("Resource not found on [{}]: {}", request.requestURI, ex.message)

        return ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body(buildError(404, "RESOURCE_NOT_FOUND", ex.message ?: "Resource not found", request))
    }

    @ExceptionHandler(ExternalApiException::class)
    fun handleExternalApi(
        ex: ExternalApiException,
        request: HttpServletRequest
    ): ResponseEntity<ErrorResponse> {
        log.error("External API error on [{}]: {}", request.requestURI, ex.message, ex)

        return ResponseEntity.status(HttpStatus.BAD_GATEWAY)
            .body(buildError(502, "EXTERNAL_API_ERROR", "Error communicating with external service", request))
    }

    // Circuit breaker aberto — serviço externo temporariamente indisponível.
    // Lança CallNotPermittedException quando o circuito está OPEN.
    @ExceptionHandler(CallNotPermittedException::class)
    fun handleCircuitBreakerOpen(
        ex: CallNotPermittedException,
        request: HttpServletRequest
    ): ResponseEntity<ErrorResponse> {
        log.warn("Circuit breaker open on [{}]: {}", request.requestURI, ex.message)

        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
            .body(buildError(503, "SERVICE_UNAVAILABLE", "Service temporarily unavailable, please try again later", request))
    }

    @ExceptionHandler(ApiException::class)
    fun handleApi(
        ex: ApiException,
        request: HttpServletRequest
    ): ResponseEntity<ErrorResponse> {
        log.error("API error on [{}]: {}", request.requestURI, ex.message, ex)

        return ResponseEntity.status(ex.status)
            .body(buildError(ex.status.value(), ex.status.name, ex.message ?: "Unexpected error", request))
    }

    @ExceptionHandler(Exception::class)
    fun handleUnexpected(
        ex: Exception,
        request: HttpServletRequest
    ): ResponseEntity<ErrorResponse> {
        log.error("Unexpected error on [{}]", request.requestURI, ex)

        return ResponseEntity.internalServerError()
            .body(buildError(500, "INTERNAL_SERVER_ERROR", "An unexpected error occurred", request))
    }

    private fun buildError(
        status: Int,
        error: String,
        message: String,
        request: HttpServletRequest
    ) = ErrorResponse(
        timestamp = LocalDateTime.now(),
        status = status,
        error = error,
        message = message,
        path = request.requestURI,
        correlationId = MDC.get(CorrelationIdFilter.MDC_KEY)
    )
}
