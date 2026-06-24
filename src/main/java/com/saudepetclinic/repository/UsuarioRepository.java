package com.saudepetclinic.repository;

import com.saudepetclinic.model.Perfil;
import com.saudepetclinic.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    Optional<Usuario> findByCpf(String cpf);

    Optional<Usuario> findByCrmv(String crmv);

    List<Usuario> findByClinica_IdClinica(Long idClinica);

    boolean existsByPerfil(Perfil perfil);
}