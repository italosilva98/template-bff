package itau.template.bff.features.example.mapper

import itau.template.bff.features.example.api.request.ExampleRequest
import itau.template.bff.features.example.api.response.ExampleResponse
import itau.template.bff.features.example.client.request.ExampleApiRequest
import itau.template.bff.features.example.client.response.ExampleApiResponse
import itau.template.bff.features.example.model.ExampleView
import org.springframework.stereotype.Component

// Isola toda transformação de dados da feature example.
//
// Fluxo de leitura:  ExampleApiResponse → ExampleView → ExampleResponse
// Fluxo de escrita:  ExampleRequest     → ExampleApiRequest
@Component
class ExampleMapper {

    // ── Leitura ───────────────────────────────────────────────────────────────

    fun toView(apiResponse: ExampleApiResponse): ExampleView =
        ExampleView(
            id = apiResponse.id,
            name = apiResponse.name,
            description = apiResponse.description ?: "",
            active = apiResponse.status.equals("ACTIVE", ignoreCase = true)
        )

    fun toResponse(view: ExampleView): ExampleResponse =
        ExampleResponse(
            id = view.id,
            name = view.name,
            description = view.description,
            active = view.active
        )

    // ── Escrita ───────────────────────────────────────────────────────────────

    // Converte o request público do BFF para o DTO que será enviado à API externa.
    fun toApiRequest(request: ExampleRequest): ExampleApiRequest =
        ExampleApiRequest(
            name = request.name,
            description = request.description,
            status = if (request.active) "ACTIVE" else "INACTIVE"
        )
}

