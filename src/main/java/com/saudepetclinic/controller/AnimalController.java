package com.saudepetclinic.controller;

import com.saudepetclinic.dto.AnimalDTO;
import com.saudepetclinic.service.AnimalService;
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
@RequestMapping(value = "/api/animais", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
@Tag(name = "Animais", description = "Pacientes vinculados a um Tutor — acesso filtrado pela clínica do usuário autenticado")
@SecurityRequirement(name = "bearerAuth")
public class AnimalController {

    private final AnimalService animalService;

    @PostMapping
    @Operation(operationId = "criarAnimal", summary = "Cadastrar animal",
            description = "O tutor (idTutor) deve pertencer à clínica do usuário autenticado.")
    public ResponseEntity<AnimalDTO> criar(@Valid @RequestBody AnimalDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(animalService.criar(dto));
    }

    @GetMapping
    @Operation(operationId = "listarAnimais", summary = "Listar animais",
            description = "ADMIN lista todos (filtro opcional idClinica). Demais listam apenas da sua clínica.")
    public ResponseEntity<List<AnimalDTO>> listar(
            @Parameter(description = "Filtro de clínica — ignorado para não-ADMIN")
            @RequestParam(required = false) Long idClinica) {
        return ResponseEntity.ok(animalService.listar(idClinica));
    }

    @PutMapping("/{id}")
    @Operation(operationId = "atualizarAnimal", summary = "Atualizar animal")
    public ResponseEntity<AnimalDTO> atualizar(@PathVariable Long id, @Valid @RequestBody AnimalDTO dto) {
        return ResponseEntity.ok(animalService.atualizar(id, dto));
    }

    @DeleteMapping("/{id}")
    @Operation(operationId = "deletarAnimal", summary = "Excluir animal",
            description = "Falha (409) se houver Agendamentos vinculados.")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        animalService.deletar(id);
        return ResponseEntity.noContent().build();
    }
}