package itau.template.bff.features.example.api

import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.runs
import io.mockk.verify
import itau.template.bff.features.example.api.response.ExampleResponse
import itau.template.bff.features.example.application.CreateExampleUseCase
import itau.template.bff.features.example.application.DeleteExampleUseCase
import itau.template.bff.features.example.application.GetExampleUseCase
import itau.template.bff.features.example.application.UpdateExampleUseCase
import itau.template.bff.shared.error.ExternalApiException
import itau.template.bff.shared.error.GlobalExceptionHandler
import itau.template.bff.shared.error.ResourceNotFoundException
import org.junit.jupiter.api.Test
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.test.web.servlet.setup.MockMvcBuilders

// Testa o controller + o GlobalExceptionHandler juntos.
// Usa standaloneSetup para evitar carregar o contexto Spring inteiro — mais rápido e sem dependências externas.
class ExampleControllerTest {

    private val getUseCase = mockk<GetExampleUseCase>()
    private val createUseCase = mockk<CreateExampleUseCase>()
    private val updateUseCase = mockk<UpdateExampleUseCase>()
    private val deleteUseCase = mockk<DeleteExampleUseCase>()

    private val mockMvc: MockMvc = MockMvcBuilders
        .standaloneSetup(ExampleController(getUseCase, createUseCase, updateUseCase, deleteUseCase))
        .setControllerAdvice(GlobalExceptionHandler())
        .build()

    // ────────────────────────────────────────────────
    // GET /{id}
    // ────────────────────────────────────────────────

    @Test
    fun `GET examples by id should return 200 with example response`() {
        val response = ExampleResponse(id = "42", name = "Widget", description = "A widget", active = true)
        every { getUseCase.execute("42") } returns response

        mockMvc.perform(get("/api/v1/examples/42").accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.id").value("42"))
            .andExpect(jsonPath("$.name").value("Widget"))
            .andExpect(jsonPath("$.description").value("A widget"))
            .andExpect(jsonPath("$.active").value(true))
    }

    @Test
    fun `GET examples by id should return 404 when resource not found`() {
        every { getUseCase.execute("99") } throws ResourceNotFoundException("Example not found: 99")

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
        every { getUseCase.execute("55") } throws ExternalApiException("External API error", externalStatus = 503)

        mockMvc.perform(get("/api/v1/examples/55").accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadGateway)
            .andExpect(jsonPath("$.status").value(502))
            .andExpect(jsonPath("$.error").value("EXTERNAL_API_ERROR"))
            .andExpect(jsonPath("$.message").value("Error communicating with external service"))
            .andExpect(jsonPath("$.timestamp").exists())
    }

    @Test
    fun `GET examples by id should return 500 and hide details on unexpected error`() {
        every { getUseCase.execute("bad") } throws RuntimeException("Internal failure")

        mockMvc.perform(get("/api/v1/examples/bad").accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isInternalServerError)
            .andExpect(jsonPath("$.status").value(500))
            .andExpect(jsonPath("$.error").value("INTERNAL_SERVER_ERROR"))
            .andExpect(jsonPath("$.message").value("An unexpected error occurred"))
    }

    // ────────────────────────────────────────────────
    // POST /
    // ────────────────────────────────────────────────

    @Test
    fun `POST examples should return 201 with created resource`() {
        val created = ExampleResponse(id = "1", name = "Widget", description = "A widget", active = true)
        every { createUseCase.execute(any()) } returns created

        val body = """{"name":"Widget","description":"A widget","active":true}"""
        mockMvc.perform(
            post("/api/v1/examples")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body)
        )
            .andExpect(status().isCreated)
            .andExpect(jsonPath("$.id").value("1"))
            .andExpect(jsonPath("$.name").value("Widget"))
    }

    @Test
    fun `POST examples should return 400 when name is blank`() {
        val body = """{"name":""}"""
        mockMvc.perform(
            post("/api/v1/examples")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body)
        )
            .andExpect(status().isBadRequest)
            .andExpect(jsonPath("$.status").value(400))
    }

    @Test
    fun `POST examples should return 502 when external api fails`() {
        every { createUseCase.execute(any()) } throws ExternalApiException("External error", externalStatus = 503)

        val body = """{"name":"Widget"}"""
        mockMvc.perform(
            post("/api/v1/examples")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body)
        )
            .andExpect(status().isBadGateway)
    }

    // ────────────────────────────────────────────────
    // PUT /{id}
    // ────────────────────────────────────────────────

    @Test
    fun `PUT examples should return 200 with updated resource`() {
        val updated = ExampleResponse(id = "42", name = "Updated", description = "", active = false)
        every { updateUseCase.execute("42", any()) } returns updated

        val body = """{"name":"Updated","active":false}"""
        mockMvc.perform(
            put("/api/v1/examples/42")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body)
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.id").value("42"))
            .andExpect(jsonPath("$.name").value("Updated"))
    }

    @Test
    fun `PUT examples should return 404 when resource not found`() {
        every { updateUseCase.execute("99", any()) } throws ResourceNotFoundException("Example not found: 99")

        val body = """{"name":"Widget"}"""
        mockMvc.perform(
            put("/api/v1/examples/99")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body)
        )
            .andExpect(status().isNotFound)
    }

    // ────────────────────────────────────────────────
    // DELETE /{id}
    // ────────────────────────────────────────────────

    @Test
    fun `DELETE examples should return 204 when deleted successfully`() {
        every { deleteUseCase.execute("42") } just runs

        mockMvc.perform(delete("/api/v1/examples/42"))
            .andExpect(status().isNoContent)

        verify(exactly = 1) { deleteUseCase.execute("42") }
    }

    @Test
    fun `DELETE examples should return 404 when resource not found`() {
        every { deleteUseCase.execute("99") } throws ResourceNotFoundException("Example not found: 99")

        mockMvc.perform(delete("/api/v1/examples/99"))
            .andExpect(status().isNotFound)
    }
}

