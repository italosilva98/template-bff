package itau.template.bff.features.example.application

import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import itau.template.bff.features.example.api.request.ExampleRequest
import itau.template.bff.features.example.client.ExampleApiClient
import itau.template.bff.features.example.client.response.ExampleApiResponse
import itau.template.bff.features.example.mapper.ExampleMapper
import itau.template.bff.shared.error.ExternalApiException
import itau.template.bff.shared.error.ResourceNotFoundException
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class UpdateExampleUseCaseTest {

    private val client = mockk<ExampleApiClient>()
    private val mapper = ExampleMapper()
    private val useCase = UpdateExampleUseCase(client, mapper)

    @Test
    fun `execute should update resource and return response`() {
        val request = ExampleRequest(name = "Updated Widget", description = "New desc", active = true)
        val apiResponse = ExampleApiResponse(id = "42", name = "Updated Widget", description = "New desc", status = "ACTIVE")
        every { client.update("42", any()) } returns apiResponse

        val result = useCase.execute("42", request)

        assertEquals("42", result.id)
        assertEquals("Updated Widget", result.name)
        verify(exactly = 1) { client.update("42", match { it.name == "Updated Widget" }) }
    }

    @Test
    fun `execute should propagate ResourceNotFoundException from client`() {
        val request = ExampleRequest(name = "Widget")
        every { client.update("99", any()) } throws ResourceNotFoundException("Example not found: 99")

        assertThrows<ResourceNotFoundException> {
            useCase.execute("99", request)
        }
    }

    @Test
    fun `execute should propagate ExternalApiException from client`() {
        val request = ExampleRequest(name = "Widget")
        every { client.update("55", any()) } throws ExternalApiException("External error", externalStatus = 503)

        assertThrows<ExternalApiException> {
            useCase.execute("55", request)
        }
    }
}
