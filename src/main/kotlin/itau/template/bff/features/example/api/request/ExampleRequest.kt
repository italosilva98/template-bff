package itau.template.bff.features.example.api.request

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

data class ExampleRequest(
    @field:NotBlank(message = "name is required")
    @field:Size(min = 1, max = 100, message = "name must be between 1 and 100 characters")
    val name: String,

    @field:Size(max = 500, message = "description must be at most 500 characters")
    val description: String? = null,

    val active: Boolean = true
)
