package com.saudepetclinic.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public record TutorDTO(

        @Schema(example = "1", accessMode = Schema.AccessMode.READ_ONLY)
        Long idTutor,

        @Schema(example = "1", description = "Id da clínica à qual este tutor pertence")
        @NotNull(message = "idClinica é obrigatório")
        Long idClinica,

        @Schema(example = "Carlos Souza")
        @NotBlank(message = "nome é obrigatório")
        String nome,

        @Schema(example = "98765432100", description = "Apenas números, 11 dígitos")
        @NotBlank(message = "cpf é obrigatório")
        @Pattern(regexp = "\\d{11}", message = "cpf deve conter exatamente 11 dígitos numéricos")
        String cpf,

        @Schema(example = "(41) 98888-1234")
        String telefone,

        @Schema(example = "Rua das Flores, 123 - Curitiba/PR")
        String endereco
) {
}
