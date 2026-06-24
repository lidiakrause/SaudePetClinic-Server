package com.saudepetclinic.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.saudepetclinic.model.Perfil;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.time.LocalTime;

public record UsuarioRequestDTO(

        @Schema(example = "1")
        Long idClinica,

        @Schema(example = "12345678901")
        @NotBlank(message = "cpf é obrigatório")
        @Pattern(regexp = "\\d{11}", message = "cpf deve conter exatamente 11 dígitos numéricos")
        String cpf,

        @Schema(example = "senha123")
        @NotBlank(message = "senha é obrigatória")
        @Size(min = 6, message = "senha deve ter no mínimo 6 caracteres")
        String senha,

        @Schema(example = "Maria Silva")
        @NotBlank(message = "nome é obrigatório")
        String nome,

        @Schema(example = "RECEPCIONISTA", description = "ADMIN, GESTOR, VETERINARIO ou RECEPCIONISTA")
        @NotNull(message = "perfil é obrigatório")
        Perfil perfil,

        @Schema(example = "CRMV-SP 12345", description = "Obrigatório e único quando perfil = VETERINARIO.")
        String crmv,

        @Schema(example = "(41) 99999-0000", description = "Opcional. Usado apenas quando perfil = VETERINARIO.")
        String telefone,

        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm")
        @Schema(example = "08:00", type = "string", pattern = "HH:mm",
                description = "Horário de início do turno Segunda-Sexta (sempre ativo). "
                        + "Obrigatório quando perfil = VETERINARIO. Formato exclusivo HH:mm.")
        LocalTime horarioInicio,

        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm")
        @Schema(example = "18:00", type = "string", pattern = "HH:mm",
                description = "Horário de fim do turno Segunda-Sexta (sempre ativo). "
                        + "Obrigatório quando perfil = VETERINARIO. Formato exclusivo HH:mm.")
        LocalTime horarioFim,

        @Schema(example = "false", description = "Se true, também atende aos Sábados, no mesmo horarioInicio–horarioFim.")
        Boolean atendeSabado,

        @Schema(example = "false", description = "Se true, também atende aos Domingos, no mesmo horarioInicio–horarioFim.")
        Boolean atendeDomingo
) {
}