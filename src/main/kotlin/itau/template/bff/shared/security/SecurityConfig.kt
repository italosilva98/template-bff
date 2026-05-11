package itau.template.bff.shared.security

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.web.SecurityFilterChain

// ATENÇÃO: Esta configuração é permissiva e serve apenas como ponto de partida.
// Para produção, substitua anyRequest().permitAll() por anyRequest().authenticated()
// e adicione a configuração do OAuth2 Resource Server:
//
//   .oauth2ResourceServer { oauth2 ->
//       oauth2.jwt { jwt -> jwt.decoder(jwtDecoder()) }
//   }
//
// Consulte o README para mais detalhes sobre integração com Cognito, Keycloak ou Azure AD.
@Configuration
@EnableWebSecurity
class SecurityConfig {

    @Bean
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {
        http
            .authorizeHttpRequests { auth ->
                auth
                    .requestMatchers(
                        "/actuator/health",
                        "/actuator/info",
                        "/swagger-ui/**",
                        "/swagger-ui.html",
                        "/v3/api-docs/**"
                    ).permitAll()
                    .anyRequest().permitAll() // TODO: substituir por .authenticated() ao integrar OAuth2
            }
            .csrf { it.disable() }
            .sessionManagement { it.sessionCreationPolicy(SessionCreationPolicy.STATELESS) }
            .httpBasic { it.disable() }

        return http.build()
    }
}
