package com.petclinic.saudepetclinic.repository;

import com.petclinic.saudepetclinic.model.Agendamento;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AgendamentoRepository
        extends JpaRepository<Agendamento, Long> {
}