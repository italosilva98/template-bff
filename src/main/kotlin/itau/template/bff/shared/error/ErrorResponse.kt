package itau.template.bff.shared.error

import java.time.LocalDateTime

data class ErrorResponse(
    val timestamp: LocalDateTime,
    val status: Int,
    val error: String,
    val message: String,
    val path: String,
    val correlationId: String?
)
