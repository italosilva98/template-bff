package itau.template.bff.features.example.mapper

import itau.template.bff.features.example.api.response.ExampleResponse
import itau.template.bff.features.example.client.response.ExampleApiResponse
import itau.template.bff.features.example.model.ExampleView
import org.springframework.stereotype.Component

// Isola toda transformação de dados da feature example.
//
// Fluxo de conversão:
//   ExampleApiResponse  →  ExampleView  →  ExampleResponse
//
// Isso garante que mudanças no contrato externo ou no contrato público
// fiquem contidas aqui, sem impactar o resto do código.
@Component
class ExampleMapper {

    // Converte o DTO externo para o modelo interno do BFF.
    // Aqui acontece a adaptação: status string → flag booleana, null → default.
    fun toView(apiResponse: ExampleApiResponse): ExampleView =
        ExampleView(
            id = apiResponse.id,
            name = apiResponse.name,
            description = apiResponse.description ?: "",
            active = apiResponse.status.equals("ACTIVE", ignoreCase = true)
        )

    // Converte o modelo interno para o DTO público do BFF.
    fun toResponse(view: ExampleView): ExampleResponse =
        ExampleResponse(
            id = view.id,
            name = view.name,
            description = view.description,
            active = view.active
        )
}
