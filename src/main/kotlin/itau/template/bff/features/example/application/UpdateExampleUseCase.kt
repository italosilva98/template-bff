package itau.template.bff.features.example.application

import itau.template.bff.features.example.api.request.ExampleRequest
import itau.template.bff.features.example.api.response.ExampleResponse
import itau.template.bff.features.example.client.ExampleApiClient
import itau.template.bff.features.example.mapper.ExampleMapper
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class UpdateExampleUseCase(
    private val exampleApiClient: ExampleApiClient,
    private val exampleMapper: ExampleMapper
) {

    private val log = LoggerFactory.getLogger(UpdateExampleUseCase::class.java)

    fun execute(id: String, request: ExampleRequest): ExampleResponse {
        log.info("Updating example id={}", id)

        val apiRequest = exampleMapper.toApiRequest(request)
        val apiResponse = exampleApiClient.update(id, apiRequest)
        val view = exampleMapper.toView(apiResponse)
        return exampleMapper.toResponse(view)
    }
}
