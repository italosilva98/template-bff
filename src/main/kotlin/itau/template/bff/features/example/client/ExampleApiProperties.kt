package itau.template.bff.features.example.client

import org.springframework.boot.context.properties.ConfigurationProperties
import java.time.Duration

// Propriedades da API externa de exemplo.
// Configuradas em application.yml sob a chave: external-apis.example-api
@ConfigurationProperties(prefix = "external-apis.example-api")
data class ExampleApiProperties(
    val baseUrl: String,
    val timeout: Duration = Duration.ofSeconds(3)
)
