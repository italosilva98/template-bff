package itau.template.bff.features.example.model

// Modelo interno do BFF — desacoplado do contrato externo e do contrato público.
// O mapper converte ExampleApiResponse -> ExampleView -> ExampleResponse.
data class ExampleView(
    val id: String,
    val name: String,
    val description: String,
    val active: Boolean
)
