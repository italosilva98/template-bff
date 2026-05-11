package itau.template.bff.features.example.api

import io.mockk.every
import io.mockk.mockk
import itau.template.bff.features.example.api.response.ExampleResponse
import itau.template.bff.features.example.application.GetExampleUseCase
import itau.template.bff.shared.error.ExternalApiException
import itau.template.bff.shared.error.GlobalExceptionHandler
import itau.template.bff.shared.error.ResourceNotFoundException
import org.junit.jupiter.api.Test
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.test.web.servlet.setup.MockMvcBuilders

// Testa o controller + o GlobalExceptionHandler juntos.
// Usa standaloneSetup para evitar carregar o contexto Spring inteiro — mais rápido e sem dependências externas.
class ExampleControllerTest {

    private val useCase = mockk<GetExampleUseCase>()

    private val mockMvc: MockMvc = MockMvcBuilders
        .standaloneSetup(ExampleController(useCase))
        .setControllerAdvice(GlobalExceptionHandler())
        .build()

    // ────────────────────────────────────────────────
    // Fluxo feliz
    // ────────────────────────────────────────────────

    @Test
    fun `GET examples by id should return 200 with example response`() {
        val response = ExampleResponse(id = "42", name = "Widget", description = "A widget", active = true)
        every { useCase.execute("42") } returns response

        mockMvc.perform(get("/api/v1/examples/42").accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.id").value("42"))
            .andExpect(jsonPath("$.name").value("Widget"))
            .andExpect(jsonPath("$.description").value("A widget"))
            .andExpect(jsonPath("$.active").value(true))
    }

    // ────────────────────────────────────────────────
    // Tratamento de erros via GlobalExceptionHandler
    // ────────────────────────────────────────────────

    @Test
    fun `GET examples by id should return 404 when resource not found`() {
        every { useCase.execute("99") } throws ResourceNotFoundException("Example not found: 99")

        mockMvc.perform(get("/api/v1/examples/99").accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNotFound)
            .andExpect(jsonPath("$.status").value(404))
            .andExpect(jsonPath("$.error").value("RESOURCE_NOT_FOUND"))
            .andExpect(jsonPath("$.message").value("Example not found: 99"))
            .andExpect(jsonPath("$.path").value("/api/v1/examples/99"))
            .andExpect(jsonPath("$.timestamp").exists())
    }

    @Test
    fun `GET examples by id should return 502 when external api fails`() {
        every { useCase.execute("55") } throws ExternalApiException("External API error", externalStatus = 503)

        mockMvc.perform(get("/api/v1/examples/55").accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadGateway)
            .andExpect(jsonPath("$.status").value(502))
            .andExpect(jsonPath("$.error").value("EXTERNAL_API_ERROR"))
            .andExpect(jsonPath("$.message").value("Error communicating with external service"))
            .andExpect(jsonPath("$.timestamp").exists())
    }

    @Test
    fun `GET examples by id should return 500 and hide details on unexpected error`() {
        every { useCase.execute("bad") } throws RuntimeException("Internal failure")

        mockMvc.perform(get("/api/v1/examples/bad").accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isInternalServerError)
            .andExpect(jsonPath("$.status").value(500))
            .andExpect(jsonPath("$.error").value("INTERNAL_SERVER_ERROR"))
            // a mensagem interna NÃO deve vazar para o cliente
            .andExpect(jsonPath("$.message").value("An unexpected error occurred"))
    }
}
