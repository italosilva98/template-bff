package itau.template.bff.shared.security

import org.springframework.stereotype.Component

// Ponto de extensão para extrair o usuário autenticado do contexto de segurança.
// Para integrar com OAuth2/JWT, injete o SecurityContext aqui:
//   val jwt = SecurityContextHolder.getContext().authentication as JwtAuthenticationToken
//   return AuthenticatedUser(id = jwt.name, name = ..., roles = jwt.authorities.map { it.authority })
@Component
class CurrentUserProvider {

    fun current(): AuthenticatedUser? = null
}
