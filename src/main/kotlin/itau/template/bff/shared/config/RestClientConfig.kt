package itau.template.bff.shared.config

import itau.template.bff.shared.observability.CorrelationIdFilter
import org.slf4j.MDC
import org.springframework.context.annotation.Configuration
import org.springframework.http.client.SimpleClientHttpRequestFactory
import org.springframework.web.client.RestClient
import java.time.Duration

// Fábrica central de RestClient.
// Cada feature client deve chamar buildClient() ao inicializar o seu RestClient,
// passando a baseUrl e timeout vindos das suas próprias @ConfigurationProperties.
//
// Exemplo de uso em uma feature:
//   @Bean
//   fun exampleRestClient(config: RestClientConfig, props: ExampleApiProperties) =
//       config.buildClient(props.baseUrl, props.timeout)
@Configuration
class RestClientConfig {

    fun buildClient(baseUrl: String, timeout: Duration): RestClient {
        val factory = SimpleClientHttpRequestFactory().apply {
            setConnectTimeout(timeout)
            setReadTimeout(timeout)
        }

        return RestClient.builder()
            .baseUrl(baseUrl)
            .requestFactory(factory)
            .requestInterceptor { request, body, execution ->
                MDC.get(CorrelationIdFilter.MDC_KEY)?.let { correlationId ->
                    request.headers[CorrelationIdFilter.HEADER_NAME] = correlationId
                }
                execution.execute(request, body)
            }
            .build()
    }
}
