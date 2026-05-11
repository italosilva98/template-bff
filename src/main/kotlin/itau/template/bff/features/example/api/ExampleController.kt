package itau.template.bff.features.example.api

import itau.template.bff.features.example.api.request.ExampleRequest
import itau.template.bff.features.example.api.response.ExampleResponse
import itau.template.bff.features.example.application.CreateExampleUseCase
import itau.template.bff.features.example.application.DeleteExampleUseCase
import itau.template.bff.features.example.application.GetExampleUseCase
import itau.template.bff.features.example.application.UpdateExampleUseCase
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.net.URI

// Controller CRUD de exemplo — serve como referência para novas features.
//
// Regras:
//   ✅ Recebe a request e delega ao UseCase correspondente.
//   ✅ Retorna os status HTTP corretos por operação.
//   ❌ NÃO chama client externo diretamente.
//   ❌ NÃO contém regra de negócio.
@RestController
@RequestMapping("/api/v1/examples")
class ExampleController(
    private val getExampleUseCase: GetExampleUseCase,
    private val createExampleUseCase: CreateExampleUseCase,
    private val updateExampleUseCase: UpdateExampleUseCase,
    private val deleteExampleUseCase: DeleteExampleUseCase
) {

    // GET /api/v1/examples/{id} → 200 OK
    @GetMapping("/{id}")
    fun getById(@PathVariable id: String): ResponseEntity<ExampleResponse> {
        val response = getExampleUseCase.execute(id)
        return ResponseEntity.ok(response)
    }

    // POST /api/v1/examples → 201 Created com Location header
    @PostMapping
    fun create(@Valid @RequestBody request: ExampleRequest): ResponseEntity<ExampleResponse> {
        val response = createExampleUseCase.execute(request)
        val location = URI.create("/api/v1/examples/${response.id}")
        return ResponseEntity.created(location).body(response)
    }

    // PUT /api/v1/examples/{id} → 200 OK
    @PutMapping("/{id}")
    fun update(
        @PathVariable id: String,
        @Valid @RequestBody request: ExampleRequest
    ): ResponseEntity<ExampleResponse> {
        val response = updateExampleUseCase.execute(id, request)
        return ResponseEntity.ok(response)
    }

    // DELETE /api/v1/examples/{id} → 204 No Content
    @DeleteMapping("/{id}")
    fun delete(@PathVariable id: String): ResponseEntity<Void> {
        deleteExampleUseCase.execute(id)
        return ResponseEntity.noContent().build()
    }
}

