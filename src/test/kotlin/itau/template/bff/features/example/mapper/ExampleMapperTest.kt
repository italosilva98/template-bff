package itau.template.bff.features.example.mapper

import itau.template.bff.features.example.client.response.ExampleApiResponse
import itau.template.bff.features.example.model.ExampleView
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class ExampleMapperTest {

    private val mapper = ExampleMapper()

    // ────────────────────────────────────────────────
    // toView
    // ────────────────────────────────────────────────

    @Test
    fun `toView should map id, name and description`() {
        val api = ExampleApiResponse(id = "1", name = "Widget", description = "A widget", status = "ACTIVE")

        val view = mapper.toView(api)

        assertEquals("1", view.id)
        assertEquals("Widget", view.name)
        assertEquals("A widget", view.description)
    }

    @Test
    fun `toView should set active true when status is ACTIVE`() {
        val api = ExampleApiResponse(id = "1", name = "X", description = null, status = "ACTIVE")

        assertTrue(mapper.toView(api).active)
    }

    @Test
    fun `toView should set active true when status is lowercase active`() {
        val api = ExampleApiResponse(id = "1", name = "X", description = null, status = "active")

        assertTrue(mapper.toView(api).active)
    }

    @Test
    fun `toView should set active false when status is INACTIVE`() {
        val api = ExampleApiResponse(id = "1", name = "X", description = null, status = "INACTIVE")

        assertFalse(mapper.toView(api).active)
    }

    @Test
    fun `toView should use empty string when description is null`() {
        val api = ExampleApiResponse(id = "1", name = "X", description = null, status = "ACTIVE")

        assertEquals("", mapper.toView(api).description)
    }

    // ────────────────────────────────────────────────
    // toResponse
    // ────────────────────────────────────────────────

    @Test
    fun `toResponse should map all fields from view`() {
        val view = ExampleView(id = "2", name = "Gadget", description = "A gadget", active = false)

        val response = mapper.toResponse(view)

        assertEquals("2", response.id)
        assertEquals("Gadget", response.name)
        assertEquals("A gadget", response.description)
        assertFalse(response.active)
    }

    @Test
    fun `full mapping pipeline should convert ACTIVE status to active true`() {
        val api = ExampleApiResponse(id = "3", name = "Thing", description = null, status = "ACTIVE")

        val response = mapper.toResponse(mapper.toView(api))

        assertEquals("3", response.id)
        assertTrue(response.active)
        assertEquals("", response.description)
    }
}
