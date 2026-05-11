package itau.template.bff.features.example.client.response

// DTO que representa exatamente o que a API externa retorna.
// Não expor esta classe fora do pacote client.
data class ExampleApiResponse(
    val id: String,
    val name: String,
    val description: String?,
    val status: String  // ex: "ACTIVE" | "INACTIVE"
)
