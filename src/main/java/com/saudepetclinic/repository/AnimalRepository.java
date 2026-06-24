package com.saudepetclinic.repository;

import com.saudepetclinic.model.Animal;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AnimalRepository extends JpaRepository<Animal, Long> {

    List<Animal> findByTutor_Clinica_IdClinica(Long idClinica);
}