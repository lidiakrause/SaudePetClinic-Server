package com.saudepetclinic.service;

import com.saudepetclinic.dto.LoginRequestDTO;
import com.saudepetclinic.dto.LoginResponseDTO;
import com.saudepetclinic.dto.UsuarioResponseDTO;
import com.saudepetclinic.exception.CredenciaisInvalidasException;
import com.saudepetclinic.model.Usuario;
import com.saudepetclinic.repository.UsuarioRepository;
import com.saudepetclinic.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    @Transactional(readOnly = true)
    public LoginResponseDTO login(LoginRequestDTO dto) {
        Usuario usuario = usuarioRepository.findByCpf(dto.cpf())
                .orElseThrow(() -> new CredenciaisInvalidasException("CPF ou senha inválidos"));

        if (!passwordEncoder.matches(dto.senha(), usuario.getSenha())) {
            throw new CredenciaisInvalidasException("CPF ou senha inválidos");
        }

        return new LoginResponseDTO(jwtService.gerarToken(usuario), toDTO(usuario));
    }

    private UsuarioResponseDTO toDTO(Usuario usuario) {
        return new UsuarioResponseDTO(
                usuario.getIdUsuario(),
                usuario.getClinica() != null ? usuario.getClinica().getIdClinica() : null,
                usuario.getClinica() != null ? usuario.getClinica().getNomeFantasia() : null,
                usuario.getCpf(),
                usuario.getNome(),
                usuario.getPerfil(),
                usuario.getCrmv(),
                usuario.getTelefone(),
                null,
                null,
                false,
                false
        );
    }
}
