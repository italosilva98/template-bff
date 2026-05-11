package itau.template.bff.features.example.application

import itau.template.bff.features.example.client.ExampleApiClient
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class DeleteExampleUseCase(
    private val exampleApiClient: ExampleApiClient
) {

    private val log = LoggerFactory.getLogger(DeleteExampleUseCase::class.java)

    fun execute(id: String) {
        log.info("Deleting example id={}", id)
        exampleApiClient.delete(id)
    }
}
