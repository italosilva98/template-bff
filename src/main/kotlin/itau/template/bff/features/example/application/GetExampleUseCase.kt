package itau.template.bff.features.example.application

import itau.template.bff.features.example.api.response.ExampleResponse
import itau.template.bff.features.example.client.ExampleApiClient
import itau.template.bff.features.example.mapper.ExampleMapper
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

// Orquestra o fluxo do caso de uso: buscar um example pelo id.
//
// Responsabilidades:
//   1. Delegar a chamada HTTP ao ExampleApiClient.
//   2. Converter a resposta via ExampleMapper.
//   3. Retornar o DTO público ExampleResponse.
//
// O UseCase NÃO:
//   - chama a API externa diretamente;
//   - faz mapping complexo inline;
//   - contém regras de negócio pesadas.
@Service
class GetExampleUseCase(
    private val exampleApiClient: ExampleApiClient,
    private val exampleMapper: ExampleMapper
) {

    private val log = LoggerFactory.getLogger(GetExampleUseCase::class.java)

    fun execute(id: String): ExampleResponse {
        log.info("Fetching example id={}", id)

        val apiResponse = exampleApiClient.getById(id)
        val view = exampleMapper.toView(apiResponse)
        return exampleMapper.toResponse(view)
    }
}
