package itau.template.bff.shared.config

import org.springframework.boot.jackson.autoconfigure.JsonMapperBuilderCustomizer
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import tools.jackson.databind.DeserializationFeature

// Jackson 3.x (Spring Boot 4) usa JsonMapperBuilderCustomizer em vez de Jackson2ObjectMapperBuilderCustomizer.
// Datas JavaTime (LocalDateTime, etc.) serializam como ISO-8601 por padrão no Jackson 3.x.
@Configuration
class JacksonConfig {

    @Bean
    fun jacksonCustomizer(): JsonMapperBuilderCustomizer =
        JsonMapperBuilderCustomizer { builder ->
            builder.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
        }
}
