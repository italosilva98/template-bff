package itau.template.bff.features.example.api.response

// DTO público do BFF — o que o cliente (front-end) enxerga.
// Nunca retornar ExampleApiResponse diretamente daqui.
data class ExampleResponse(
    val id: String,
    val name: String,
    val description: String,
    val active: Boolean
)
