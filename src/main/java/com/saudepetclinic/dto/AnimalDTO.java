package com.saudepetclinic.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record AnimalDTO(

        @Schema(example = "1", accessMode = Schema.AccessMode.READ_ONLY)
        Long idAnimal,

        @Schema(example = "1", description = "Id do Tutor responsável pelo animal")
        @NotNull(message = "idTutor é obrigatório")
        Long idTutor,

        @Schema(example = "Rex")
        @NotBlank(message = "nome é obrigatório")
        String nome,

        @Schema(example = "Canina")
        @NotBlank(message = "especie é obrigatório")
        String especie,

        @Schema(example = "Labrador")
        String raca,

        @Schema(example = "3", description = "Idade em anos")
        @Min(value = 0, message = "idade não pode ser negativa")
        Integer idade,

        @Schema(example = "22.50", description = "Peso em quilogramas")
        @DecimalMin(value = "0.0", inclusive = false, message = "peso deve ser maior que zero")
        BigDecimal peso,

        @Schema(example = "MACHO", description = "Ex: MACHO ou FEMEA")
        String sexo
) {
}
