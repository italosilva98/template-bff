package itau.template.bff.features.example.application

import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.runs
import io.mockk.verify
import itau.template.bff.features.example.client.ExampleApiClient
import itau.template.bff.shared.error.ExternalApiException
import itau.template.bff.shared.error.ResourceNotFoundException
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class DeleteExampleUseCaseTest {

    private val client = mockk<ExampleApiClient>()
    private val useCase = DeleteExampleUseCase(client)

    @Test
    fun `execute should delete resource successfully`() {
        every { client.delete("42") } just runs

        useCase.execute("42")

        verify(exactly = 1) { client.delete("42") }
    }

    @Test
    fun `execute should propagate ResourceNotFoundException from client`() {
        every { client.delete("99") } throws ResourceNotFoundException("Example not found: 99")

        assertThrows<ResourceNotFoundException> {
            useCase.execute("99")
        }
    }

    @Test
    fun `execute should propagate ExternalApiException from client`() {
        every { client.delete("55") } throws ExternalApiException("External error", externalStatus = 503)

        assertThrows<ExternalApiException> {
            useCase.execute("55")
        }
    }
}
