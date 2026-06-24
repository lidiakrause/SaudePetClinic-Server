package com.saudepetclinic.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record LoginRequestDTO(

        @Schema(example = "12345678901", description = "Apenas números, 11 dígitos")
        @NotBlank(message = "cpf é obrigatório")
        @Pattern(regexp = "\\d{11}", message = "cpf deve conter exatamente 11 dígitos numéricos")
        String cpf,

        @Schema(example = "senha123")
        @NotBlank(message = "senha é obrigatória")
        String senha
) {
}
