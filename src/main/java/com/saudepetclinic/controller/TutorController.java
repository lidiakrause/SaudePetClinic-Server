package com.saudepetclinic.controller;

import com.saudepetclinic.dto.TutorDTO;
import com.saudepetclinic.service.TutorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/api/tutores", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
@Tag(name = "Tutores", description = "Responsáveis pelos animais — acesso filtrado pela clínica do usuário autenticado")
@SecurityRequirement(name = "bearerAuth")
public class TutorController {

    private final TutorService tutorService;

    @PostMapping
    @Operation(operationId = "criarTutor", summary = "Cadastrar tutor",
            description = "Não-ADMIN: idClinica é ignorado e substituído pela clínica do usuário autenticado.")
    public ResponseEntity<TutorDTO> criar(@Valid @RequestBody TutorDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(tutorService.criar(dto));
    }

    @GetMapping
    @Operation(operationId = "listarTutores", summary = "Listar tutores",
            description = "ADMIN lista todos (filtro opcional idClinica). Demais listam apenas da sua clínica.")
    public ResponseEntity<List<TutorDTO>> listar(
            @Parameter(description = "Filtro de clínica — ignorado para não-ADMIN")
            @RequestParam(required = false) Long idClinica) {
        return ResponseEntity.ok(tutorService.listar(idClinica));
    }

    @PutMapping("/{id}")
    @Operation(operationId = "atualizarTutor", summary = "Atualizar tutor")
    public ResponseEntity<TutorDTO> atualizar(@PathVariable Long id, @Valid @RequestBody TutorDTO dto) {
        return ResponseEntity.ok(tutorService.atualizar(id, dto));
    }

    @DeleteMapping("/{id}")
    @Operation(operationId = "deletarTutor", summary = "Excluir tutor",
            description = "Falha (409) se houver animais vinculados.")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        tutorService.deletar(id);
        return ResponseEntity.noContent().build();
    }
}