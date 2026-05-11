package itau.template.bff.features.example.application

import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import itau.template.bff.features.example.client.ExampleApiClient
import itau.template.bff.features.example.client.response.ExampleApiResponse
import itau.template.bff.features.example.mapper.ExampleMapper
import itau.template.bff.shared.error.ExternalApiException
import itau.template.bff.shared.error.ResourceNotFoundException
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class GetExampleUseCaseTest {

    private val client = mockk<ExampleApiClient>()
    private val mapper = ExampleMapper()                       // usa implementação real — sem mock desnecessário
    private val useCase = GetExampleUseCase(client, mapper)

    @Test
    fun `execute should return response when client succeeds`() {
        val apiResponse = ExampleApiResponse(
            id = "42",
            name = "Widget",
            description = "A great widget",
            status = "ACTIVE"
        )
        every { client.getById("42") } returns apiResponse

        val result = useCase.execute("42")

        assertEquals("42", result.id)
        assertEquals("Widget", result.name)
        assertEquals("A great widget", result.description)
        assertTrue(result.active)
        verify(exactly = 1) { client.getById("42") }
    }

    @Test
    fun `execute should propagate ResourceNotFoundException from client`() {
        every { client.getById("99") } throws ResourceNotFoundException("Example not found: 99")

        assertThrows<ResourceNotFoundException> {
            useCase.execute("99")
        }
    }

    @Test
    fun `execute should propagate ExternalApiException from client`() {
        every { client.getById("77") } throws ExternalApiException("External API error", externalStatus = 503)

        assertThrows<ExternalApiException> {
            useCase.execute("77")
        }
    }

    @Test
    fun `execute should convert INACTIVE status to active false`() {
        every { client.getById("10") } returns ExampleApiResponse(
            id = "10",
            name = "OldItem",
            description = null,
            status = "INACTIVE"
        )

        val result = useCase.execute("10")

        assertEquals(false, result.active)
        assertEquals("", result.description)
    }
}
