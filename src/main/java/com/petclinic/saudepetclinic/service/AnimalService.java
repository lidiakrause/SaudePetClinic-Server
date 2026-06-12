package com.petclinic.saudepetclinic.service;

import com.petclinic.saudepetclinic.dto.AnimalDTO;
import com.petclinic.saudepetclinic.model.Animal;
import com.petclinic.saudepetclinic.model.Tutor;
import com.petclinic.saudepetclinic.repository.AnimalRepository;
import com.petclinic.saudepetclinic.repository.TutorRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AnimalService {

    private final AnimalRepository animalRepository;
    private final TutorRepository tutorRepository;

    public AnimalService(
            AnimalRepository animalRepository,
            TutorRepository tutorRepository
    ) {
        this.animalRepository = animalRepository;
        this.tutorRepository = tutorRepository;
    }

    public Animal salvar(AnimalDTO dto) {

        Tutor tutor = tutorRepository.findById(dto.getTutorId())
                .orElseThrow(() -> new RuntimeException("Tutor não encontrado"));

        Animal animal = new Animal();

        animal.setNome(dto.getNome());
        animal.setEspecie(dto.getEspecie());
        animal.setRaca(dto.getRaca());
        animal.setSexo(dto.getSexo());
        animal.setTutor(tutor);

        return animalRepository.save(animal);
    }

    public List<Animal> listar() {
        return animalRepository.findAll();
    }

}