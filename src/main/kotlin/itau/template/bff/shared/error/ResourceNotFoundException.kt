package itau.template.bff.shared.error

import org.springframework.http.HttpStatus

class ResourceNotFoundException(message: String) : ApiException(message, HttpStatus.NOT_FOUND)
