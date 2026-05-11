package itau.template.bff.shared.error

import org.springframework.http.HttpStatus

open class ApiException(
    message: String,
    val status: HttpStatus = HttpStatus.INTERNAL_SERVER_ERROR,
    cause: Throwable? = null
) : RuntimeException(message, cause)
