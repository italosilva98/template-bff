package itau.template.bff.features.example.client.request

// DTO que representa o payload enviado para a API externa.
// Nunca expor esta classe fora do pacote client.
data class ExampleApiRequest(
    val name: String,
    val description: String?,
    val status: String  // "ACTIVE" | "INACTIVE"
)
