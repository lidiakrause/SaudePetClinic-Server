package com.saudepetclinic.repository;

import com.saudepetclinic.model.AgendaConfigVeterinario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AgendaConfigVeterinarioRepository extends JpaRepository<AgendaConfigVeterinario, Long> {

    List<AgendaConfigVeterinario> findByUsuario_IdUsuario(Long idUsuario);

    List<AgendaConfigVeterinario> findByUsuario_IdUsuarioAndDiaSemana(Long idUsuario, Integer diaSemana);
}