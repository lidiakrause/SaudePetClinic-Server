package com.saudepetclinic.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.saudepetclinic.model.StatusAgendamento;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;
import java.time.LocalTime;

public record AgendamentoResponseDTO(

        @Schema(example = "1")
        Long idAgendamento,

        @Schema(example = "1")
        Long idAnimal,

        @Schema(example = "Rex")
        String nomeAnimal,

        @Schema(example = "1")
        Long idVet,

        @Schema(example = "João Pereira")
        String nomeVeterinario,

        @Schema(example = "2026-06-22", type = "string")
        LocalDate data,

        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm")
        @Schema(example = "09:30", type = "string", pattern = "HH:mm")
        LocalTime hora,

        @Schema(example = "AGENDADO")
        StatusAgendamento status,

        @Schema(example = "Vacina antirrábica anual")
        String anotacoes
) {
}
