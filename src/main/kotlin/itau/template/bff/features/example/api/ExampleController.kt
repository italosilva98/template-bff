package itau.template.bff.features.example.api

import itau.template.bff.features.example.api.response.ExampleResponse
import itau.template.bff.features.example.application.GetExampleUseCase
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

// Controller de exemplo — serve como referência para novas features.
//
// Regras deste controller:
//   ✅ Recebe a request HTTP e valida a entrada básica.
//   ✅ Delega o processamento para o UseCase.
//   ✅ Retorna a response padronizada.
//   ❌ NÃO chama client externo diretamente.
//   ❌ NÃO contém regra de negócio.
//   ❌ NÃO faz mapping complexo.
@RestController
@RequestMapping("/api/v1/examples")
class ExampleController(
    private val getExampleUseCase: GetExampleUseCase
) {

    @GetMapping("/{id}")
    fun getById(@PathVariable id: String): ResponseEntity<ExampleResponse> {
        val response = getExampleUseCase.execute(id)
        return ResponseEntity.ok(response)
    }
}
