package com.petclinic.saudepetclinic.service;

import com.petclinic.saudepetclinic.dto.AgendamentoDTO;
import com.petclinic.saudepetclinic.model.Agendamento;
import com.petclinic.saudepetclinic.model.Animal;
import com.petclinic.saudepetclinic.repository.AgendamentoRepository;
import com.petclinic.saudepetclinic.repository.AnimalRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AgendamentoService {

    private final AgendamentoRepository agendamentoRepository;
    private final AnimalRepository animalRepository;

    public AgendamentoService(
            AgendamentoRepository agendamentoRepository,
            AnimalRepository animalRepository
    ) {
        this.agendamentoRepository = agendamentoRepository;
        this.animalRepository = animalRepository;
    }

    public Agendamento salvar(AgendamentoDTO dto) {

        Animal animal = animalRepository.findById(dto.getAnimalId())
                .orElseThrow(() ->
                        new RuntimeException("Animal não encontrado"));

        Agendamento agendamento = new Agendamento();

        agendamento.setData(dto.getData());
        agendamento.setHora(dto.getHora());
        agendamento.setTipo(dto.getTipo());
        agendamento.setStatus(dto.getStatus());
        agendamento.setAnimal(animal);

        return agendamentoRepository.save(agendamento);
    }

    public List<Agendamento> listar() {
        return agendamentoRepository.findAll();
    }
}