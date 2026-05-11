package itau.template.bff.shared.documentation

import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.info.Contact
import io.swagger.v3.oas.models.info.Info
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class OpenApiConfig {

    @Bean
    fun openApi(): OpenAPI = OpenAPI()
        .info(
            Info()
                .title("BFF Scaffold API")
                .description("Backend for Frontend — template base para novos BFFs Kotlin")
                .version("1.0.0")
                .contact(Contact().name("Platform Team"))
        )
}
