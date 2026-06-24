package com.saudepetclinic.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.time.LocalTime;

public record AgendamentoRequestDTO(

        @Schema(example = "1")
        @NotNull(message = "idAnimal é obrigatório")
        Long idAnimal,

        @Schema(example = "1")
        @NotNull(message = "idVet é obrigatório")
        Long idVet,

        @Schema(example = "2026-06-22", type = "string", description = "Data da consulta (hoje ou futura)")
        @NotNull(message = "data é obrigatória")
        @FutureOrPresent(message = "data não pode ser no passado")
        LocalDate data,

        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm")
        @Schema(example = "09:30", type = "string", pattern = "HH:mm",
                description = "Horário da consulta, formato exclusivo HH:mm. Deve estar dentro do turno do veterinário.")
        @NotNull(message = "hora é obrigatória")
        LocalTime hora,

        @Schema(example = "Vacina antirrábica anual")
        String anotacoes
) {
}
