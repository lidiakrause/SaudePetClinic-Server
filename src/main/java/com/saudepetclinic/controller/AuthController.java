package com.saudepetclinic.controller;

import com.saudepetclinic.dto.LoginRequestDTO;
import com.saudepetclinic.dto.LoginResponseDTO;
import com.saudepetclinic.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/api/auth", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
@Tag(name = "Autenticacao", description = "Login unificado por CPF e senha para todos os perfis")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    @Operation(
            operationId = "login",
            summary = "Login unificado (todos os perfis)",
            description = "Autentica qualquer usuário (ADMIN, GESTOR, VETERINARIO ou RECEPCIONISTA) "
                    + "pelo CPF + senha. Retorna um token JWT que deve ser enviado no header "
                    + "'Authorization: Bearer {token}' para acessar rotas protegidas. "
                    + "Retorna 401 se CPF/senha forem inválidos.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Login efetuado com sucesso",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = LoginResponseDTO.class)
                            )
                    ),
                    @ApiResponse(responseCode = "401", description = "Credenciais inválidas")
            }
    )
    public ResponseEntity<LoginResponseDTO> login(@Valid @RequestBody LoginRequestDTO dto) {
        return ResponseEntity.ok(authService.login(dto));
    }
}