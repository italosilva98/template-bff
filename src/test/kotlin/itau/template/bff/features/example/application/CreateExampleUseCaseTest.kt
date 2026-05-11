package itau.template.bff.features.example.application

import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import itau.template.bff.features.example.api.request.ExampleRequest
import itau.template.bff.features.example.client.ExampleApiClient
import itau.template.bff.features.example.client.response.ExampleApiResponse
import itau.template.bff.features.example.mapper.ExampleMapper
import itau.template.bff.shared.error.ExternalApiException
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class CreateExampleUseCaseTest {

    private val client = mockk<ExampleApiClient>()
    private val mapper = ExampleMapper()
    private val useCase = CreateExampleUseCase(client, mapper)

    @Test
    fun `execute should create resource and return response`() {
        val request = ExampleRequest(name = "Widget", description = "A widget", active = true)
        val apiResponse = ExampleApiResponse(id = "1", name = "Widget", description = "A widget", status = "ACTIVE")
        every { client.create(any()) } returns apiResponse

        val result = useCase.execute(request)

        assertEquals("1", result.id)
        assertEquals("Widget", result.name)
        assertEquals("A widget", result.description)
        assertTrue(result.active)
        verify(exactly = 1) { client.create(match { it.name == "Widget" && it.status == "ACTIVE" }) }
    }

    @Test
    fun `execute should map inactive request to INACTIVE status`() {
        val request = ExampleRequest(name = "Archived", active = false)
        val apiResponse = ExampleApiResponse(id = "2", name = "Archived", description = null, status = "INACTIVE")
        every { client.create(any()) } returns apiResponse

        val result = useCase.execute(request)

        verify { client.create(match { it.status == "INACTIVE" }) }
        assertTrue(!result.active)
    }

    @Test
    fun `execute should propagate ExternalApiException from client`() {
        val request = ExampleRequest(name = "Widget")
        every { client.create(any()) } throws ExternalApiException("External error", externalStatus = 500)

        assertThrows<ExternalApiException> {
            useCase.execute(request)
        }
    }
}
