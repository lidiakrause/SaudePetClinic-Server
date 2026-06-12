package com.petclinic.saudepetclinic.service;

import com.petclinic.saudepetclinic.dto.TutorDTO;
import com.petclinic.saudepetclinic.model.Tutor;
import com.petclinic.saudepetclinic.repository.TutorRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TutorService {

    private final TutorRepository repository;

    public TutorService(TutorRepository repository) {
        this.repository = repository;
    }

    public Tutor salvar(TutorDTO dto) {

        Tutor tutor = new Tutor();

        tutor.setNome(dto.getNome());
        tutor.setCpf(dto.getCpf());
        tutor.setTelefone(dto.getTelefone());

        return repository.save(tutor);
    }

    public List<Tutor> listar() {
        return repository.findAll();
    }
}