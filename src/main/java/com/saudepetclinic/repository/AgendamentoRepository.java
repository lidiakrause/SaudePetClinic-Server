package com.saudepetclinic.repository;

import com.saudepetclinic.model.Agendamento;
import com.saudepetclinic.model.StatusAgendamento;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public interface AgendamentoRepository extends JpaRepository<Agendamento, Long> {

    List<Agendamento> findByVeterinario_IdUsuarioAndData(Long idUsuario, LocalDate data);

    List<Agendamento> findByAnimal_IdAnimal(Long idAnimal);

    List<Agendamento> findByVeterinario_IdUsuarioAndDataAndHoraAndStatusNot(
            Long idUsuario, LocalDate data, LocalTime hora, StatusAgendamento status);

    List<Agendamento> findByAnimal_IdAnimalAndDataAndHoraAndStatusNot(
            Long idAnimal, LocalDate data, LocalTime hora, StatusAgendamento status);

    List<Agendamento> findByVeterinario_Clinica_IdClinica(Long idClinica);
}   