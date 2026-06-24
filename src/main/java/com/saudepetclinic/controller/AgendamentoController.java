package com.saudepetclinic.controller;

import com.saudepetclinic.dto.AgendamentoRequestDTO;
import com.saudepetclinic.dto.AgendamentoResponseDTO;
import com.saudepetclinic.dto.AgendamentoStatusUpdateDTO;
import com.saudepetclinic.service.AgendamentoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping(value = "/api/agendamentos", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
@Tag(name = "Agendamentos", description = "Consultas agendadas — acesso filtrado pela clínica do usuário autenticado")
@SecurityRequirement(name = "bearerAuth")
public class AgendamentoController {

    private final AgendamentoService agendamentoService;

    @PostMapping
    @Operation(operationId = "criarAgendamento", summary = "Criar agendamento",
            description = "Valida expediente e conflito de horário. Animal e veterinário devem pertencer "
                    + "à mesma clínica do usuário autenticado.")
    public ResponseEntity<AgendamentoResponseDTO> criar(@Valid @RequestBody AgendamentoRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(agendamentoService.criar(dto));
    }

    @GetMapping
    @Operation(operationId = "listarAgendamentos", summary = "Listar agendamentos",
            description = "ADMIN lista todos. Demais listam apenas da sua clínica. "
                    + "Filtros: idAnimal (histórico) ou idVet + data (agenda do dia). "
                    + "Se idVet for informado, data também deve ser informada.")
    public ResponseEntity<List<AgendamentoResponseDTO>> listar(
            @Parameter(description = "Histórico de um animal específico")
            @RequestParam(required = false) Long idAnimal,
            @Parameter(description = "Agenda de um veterinário (use com 'data')")
            @RequestParam(required = false) Long idVet,
            @Parameter(description = "Data no formato yyyy-MM-dd, usada com idVet")
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate data) {
        return ResponseEntity.ok(agendamentoService.listar(idAnimal, idVet, data));
    }

    @PutMapping("/{id}")
    @Operation(operationId = "atualizarAgendamento", summary = "Atualizar consulta (reagendar/anotações)",
            description = "Reaplica validações de expediente e conflito. "
                    + "Não permite alterar agendamentos FINALIZADO ou CANCELADO.")
    public ResponseEntity<AgendamentoResponseDTO> atualizar(
            @PathVariable Long id, @Valid @RequestBody AgendamentoRequestDTO dto) {
        return ResponseEntity.ok(agendamentoService.atualizar(id, dto));
    }

    @PatchMapping("/{id}/status")
    @Operation(operationId = "atualizarStatusAgendamento", summary = "Atualizar status do agendamento",
            description = "Transições permitidas a partir de AGENDADO: FINALIZADO ou CANCELADO. "
                    + "Não é possível reverter para AGENDADO. O campo 'anotacoes' é obrigatório "
                    + "tanto para FINALIZADO quanto para CANCELADO.")
    public ResponseEntity<AgendamentoResponseDTO> atualizarStatus(
            @PathVariable Long id, @Valid @RequestBody AgendamentoStatusUpdateDTO dto) {
        return ResponseEntity.ok(agendamentoService.atualizarStatus(id, dto.status(), dto.anotacoes()));
    }
}