package com.saudepetclinic.controller;

import com.saudepetclinic.dto.UsuarioRequestDTO;
import com.saudepetclinic.dto.UsuarioResponseDTO;
import com.saudepetclinic.dto.UsuarioUpdateDTO;
import com.saudepetclinic.service.UsuarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/api/usuarios", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
@Tag(name = "Usuarios", description = "Equipe médica e administrativa — leitura liberada a todos os perfis da clínica, escrita restrita a ADMIN/GESTOR")
@SecurityRequirement(name = "bearerAuth")
public class UsuarioController {

    private final UsuarioService usuarioService;

    @PostMapping
    @Operation(operationId = "criarUsuario", summary = "Cadastrar usuário",
            description = "ADMIN pode criar qualquer perfil em qualquer clínica. "
                    + "GESTOR só pode criar VETERINARIO ou RECEPCIONISTA na sua própria clínica.")
    public ResponseEntity<UsuarioResponseDTO> criar(@Valid @RequestBody UsuarioRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(usuarioService.criar(dto));
    }

    @GetMapping
    @Operation(operationId = "listarUsuarios", summary = "Listar usuários",
            description = "ADMIN lista todos (filtro opcional idClinica). "
                    + "Demais perfis listam apenas sua própria clínica (filtro ignorado).")
    public ResponseEntity<List<UsuarioResponseDTO>> listar(
            @Parameter(description = "Filtro de clínica — ignorado para não-ADMIN")
            @RequestParam(required = false) Long idClinica) {
        return ResponseEntity.ok(usuarioService.listar(idClinica));
    }

    @PutMapping("/{id}")
    @Operation(operationId = "atualizarUsuario", summary = "Atualizar usuário",
            description = "GESTOR só pode atualizar usuários da sua clínica. Senha é opcional: "
                    + "se omitida, a senha atual é mantida.")
    public ResponseEntity<UsuarioResponseDTO> atualizar(
            @PathVariable Long id, @Valid @RequestBody UsuarioUpdateDTO dto) {
        return ResponseEntity.ok(usuarioService.atualizar(id, dto));
    }

    @DeleteMapping("/{id}")
    @Operation(operationId = "deletarUsuario", summary = "Excluir usuário",
            description = "GESTOR só pode excluir usuários da sua clínica. "
                    + "Falha (409) se houver Agendamentos vinculados.")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        usuarioService.deletar(id);
        return ResponseEntity.noContent().build();
    }
}