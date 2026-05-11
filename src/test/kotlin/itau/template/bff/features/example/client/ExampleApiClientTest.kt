package itau.template.bff.features.example.client

import com.github.tomakehurst.wiremock.client.WireMock.aResponse
import com.github.tomakehurst.wiremock.client.WireMock.get
import com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo
import com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig
import com.github.tomakehurst.wiremock.junit5.WireMockExtension
import itau.template.bff.shared.config.RestClientConfig
import itau.template.bff.shared.error.ExternalApiException
import itau.template.bff.shared.error.ResourceNotFoundException
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.RegisterExtension
import java.time.Duration

class ExampleApiClientTest {

    companion object {
        @RegisterExtension
        @JvmField
        val wireMock: WireMockExtension = WireMockExtension.newInstance()
            .options(wireMockConfig().dynamicPort())
            .build()
    }

    private lateinit var client: ExampleApiClient

    @BeforeEach
    fun setup() {
        val properties = ExampleApiProperties(
            baseUrl = wireMock.baseUrl(),
            timeout = Duration.ofSeconds(5)
        )
        client = ExampleApiClient(RestClientConfig(), properties)
    }

    @Test
    fun `getById should return ExampleApiResponse when api returns 200`() {
        wireMock.stubFor(
            get(urlEqualTo("/examples/123"))
                .willReturn(
                    aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(
                            """
                            {
                              "id": "123",
                              "name": "Test Widget",
                              "description": "A test widget",
                              "status": "ACTIVE"
                            }
                            """.trimIndent()
                        )
                )
        )

        val response = client.getById("123")

        assertEquals("123", response.id)
        assertEquals("Test Widget", response.name)
        assertEquals("A test widget", response.description)
        assertEquals("ACTIVE", response.status)
    }

    @Test
    fun `getById should handle null description from api`() {
        wireMock.stubFor(
            get(urlEqualTo("/examples/456"))
                .willReturn(
                    aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("""{"id":"456","name":"Minimal","status":"INACTIVE"}""")
                )
        )

        val response = client.getById("456")

        assertEquals("456", response.id)
        assertTrue(response.description == null)
    }

    @Test
    fun `getById should throw ResourceNotFoundException when api returns 404`() {
        wireMock.stubFor(
            get(urlEqualTo("/examples/999"))
                .willReturn(aResponse().withStatus(404))
        )

        assertThrows<ResourceNotFoundException> {
            client.getById("999")
        }
    }

    @Test
    fun `getById should throw ExternalApiException when api returns 500`() {
        wireMock.stubFor(
            get(urlEqualTo("/examples/broken"))
                .willReturn(aResponse().withStatus(500))
        )

        assertThrows<ExternalApiException> {
            client.getById("broken")
        }
    }

    @Test
    fun `getById should throw ExternalApiException when api returns 503`() {
        wireMock.stubFor(
            get(urlEqualTo("/examples/down"))
                .willReturn(aResponse().withStatus(503))
        )

        assertThrows<ExternalApiException> {
            client.getById("down")
        }
    }
}
