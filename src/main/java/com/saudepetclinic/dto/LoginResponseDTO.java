package com.saudepetclinic.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public record LoginResponseDTO(

        @Schema(example = "eyJhbGciOiJIUzI1NiJ9...", description = "Token JWT. Envie como 'Authorization: Bearer {token}'")
        String token,

        UsuarioResponseDTO usuario
) {
}
