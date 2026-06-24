package com.saudepetclinic.dto;

import com.saudepetclinic.model.StatusAgendamento;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

public record AgendamentoStatusUpdateDTO(

        @Schema(example = "FINALIZADO", description = "FINALIZADO ou CANCELADO")
        @NotNull(message = "status é obrigatório")
        StatusAgendamento status,

        @Schema(example = "Vacina aplicada, animal saudável. Retorno em 30 dias.",
                description = "Obrigatório ao finalizar ou cancelar.")
        String anotacoes
) {
}