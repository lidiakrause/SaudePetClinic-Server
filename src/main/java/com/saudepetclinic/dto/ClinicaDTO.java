package com.saudepetclinic.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record ClinicaDTO(

        @Schema(example = "1", accessMode = Schema.AccessMode.READ_ONLY)
        Long idClinica,

        @Schema(example = "12345678000190", description = "Apenas números, 14 dígitos. Usado no login institucional.")
        @NotBlank(message = "cnpj é obrigatório")
        @Pattern(regexp = "\\d{14}", message = "cnpj deve conter exatamente 14 dígitos numéricos")
        String cnpj,

        @Schema(example = "Clinica Saude Pet LTDA")
        @NotBlank(message = "razaoSocial é obrigatório")
        String razaoSocial,

        @Schema(example = "Saude Pet Clinic - Unidade Centro")
        String nomeFantasia
) {
}
