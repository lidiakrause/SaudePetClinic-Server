package com.saudepetclinic.repository;

import com.saudepetclinic.model.Tutor;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TutorRepository extends JpaRepository<Tutor, Long> {

    List<Tutor> findByClinica_IdClinica(Long idClinica);

    Optional<Tutor> findByCpfAndClinica_IdClinica(String cpf, Long idClinica);
}
