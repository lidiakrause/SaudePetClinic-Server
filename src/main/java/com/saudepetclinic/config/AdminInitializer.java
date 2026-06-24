package com.saudepetclinic.config;

import com.saudepetclinic.model.Perfil;
import com.saudepetclinic.model.Usuario;
import com.saudepetclinic.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class AdminInitializer implements ApplicationRunner {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${ADMIN_INITIAL_CPF}")
    private String cpf;

    @Value("${ADMIN_INITIAL_PASSWORD}")
    private String senha;

    @Value("${ADMIN_INITIAL_NOME}")
    private String nome;

    @Override
    public void run(ApplicationArguments args) {
        if (usuarioRepository.existsByPerfil(Perfil.ADMIN)) {
            return;
        }

        Usuario admin = new Usuario();
        admin.setCpf(cpf);
        admin.setSenha(passwordEncoder.encode(senha));
        admin.setNome(nome);
        admin.setPerfil(Perfil.ADMIN);
        admin.setClinica(null);

        usuarioRepository.save(admin);
        log.info("Admin inicial criado!");
    }
}
