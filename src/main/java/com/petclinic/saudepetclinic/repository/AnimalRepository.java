package com.petclinic.saudepetclinic.repository;

import com.petclinic.saudepetclinic.model.Animal;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AnimalRepository extends JpaRepository<Animal, Long> {
}