package com.saudepetclinic.controller;

import com.saudepetclinic.dto.ClinicaDTO;
import com.saudepetclinic.service.ClinicaService;
import io.swagger.v3.oas.annotations.Operation;
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
@RequestMapping(path = "/api/clinicas", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
@Tag(name = "Clinicas", description = "Cadastro das unidades/filiais. Acesso restrito a usuários ADMIN.")
@SecurityRequirement(name = "bearerAuth")
public class ClinicaController {

    private final ClinicaService clinicaService;

    @PostMapping
    @Operation(operationId = "criarClinica", summary = "Cadastrar uma nova clínica [ADMIN]")
    public ResponseEntity<ClinicaDTO> criar(@Valid @RequestBody ClinicaDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(clinicaService.criar(dto));
    }

    @GetMapping
    @Operation(operationId = "listarClinicas", summary = "Listar todas as clínicas [ADMIN]")
    public ResponseEntity<List<ClinicaDTO>> listarTodas() {
        return ResponseEntity.ok(clinicaService.listarTodas());
    }

    @PutMapping("/{id}")
    @Operation(operationId = "atualizarClinica", summary = "Atualizar dados da clínica [ADMIN]")
    public ResponseEntity<ClinicaDTO> atualizar(@PathVariable Long id, @Valid @RequestBody ClinicaDTO dto) {
        return ResponseEntity.ok(clinicaService.atualizar(id, dto));
    }

    @DeleteMapping("/{id}")
    @Operation(operationId = "deletarClinica", summary = "Excluir clínica [ADMIN]",
            description = "Falha (409) se houver usuários ou tutores vinculados a esta clínica.")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        clinicaService.deletar(id);
        return ResponseEntity.noContent().build();
    }
}