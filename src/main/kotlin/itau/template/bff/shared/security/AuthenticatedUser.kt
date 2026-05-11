package itau.template.bff.shared.security

data class AuthenticatedUser(
    val id: String,
    val name: String,
    val roles: List<String>
)
