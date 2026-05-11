package itau.template.bff.shared.error

import org.springframework.http.HttpStatus

class ExternalApiException(
    message: String,
    val externalStatus: Int? = null,
    cause: Throwable? = null
) : ApiException(message, HttpStatus.BAD_GATEWAY, cause)
