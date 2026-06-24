package com.saudepetclinic.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.saudepetclinic.model.Perfil;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalTime;

public record UsuarioResponseDTO(
        @Schema(example = "1") Long idUsuario,
        @Schema(example = "1", description = "Nulo quando perfil = ADMIN") Long idClinica,
        @Schema(example = "Clínica Saúde Pet", description = "Nulo quando perfil = ADMIN") String nomeClinica,
        @Schema(example = "12345678901") String cpf,
        @Schema(example = "Maria Silva") String nome,
        @Schema(example = "RECEPCIONISTA") Perfil perfil,
        @Schema(example = "CRMV-SP 12345") String crmv,
        @Schema(example = "(41) 99999-0000") String telefone,

        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm")
        @Schema(example = "08:00", type = "string", pattern = "HH:mm",
                description = "Turno Segunda-Sexta. Nulo quando perfil != VETERINARIO")
        LocalTime horarioInicio,

        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm")
        @Schema(example = "18:00", type = "string", pattern = "HH:mm",
                description = "Turno Segunda-Sexta. Nulo quando perfil != VETERINARIO")
        LocalTime horarioFim,

        @Schema(example = "true") boolean atendeSabado,
        @Schema(example = "true") boolean atendeDomingo
) {
}