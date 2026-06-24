package com.saudepetclinic.service;

import com.saudepetclinic.dto.AnimalDTO;
import com.saudepetclinic.exception.ResourceNotFoundException;
import com.saudepetclinic.model.Animal;
import com.saudepetclinic.model.Tutor;
import com.saudepetclinic.repository.AnimalRepository;
import com.saudepetclinic.security.AuthContext;
import com.saudepetclinic.security.AuthContextResolver;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AnimalService {

    private final AnimalRepository animalRepository;
    private final TutorService tutorService;
    private final ClinicaService clinicaService;

    @Transactional
    public AnimalDTO criar(AnimalDTO dto) {
        AuthContext ctx = AuthContextResolver.get();
        Tutor tutor = tutorService.buscarEntidadePorId(dto.idTutor());

        if (!ctx.isAdmin() && !ctx.pertenceAClinica(tutor.getClinica().getIdClinica())) {
            throw new AccessDeniedException("Acesso negado: tutor pertence a outra clínica");
        }

        Animal animal = new Animal();
        animal.setTutor(tutor);
        animal.setNome(dto.nome());
        animal.setEspecie(dto.especie());
        animal.setRaca(dto.raca());
        animal.setIdade(dto.idade());
        animal.setPeso(dto.peso());
        animal.setSexo(dto.sexo());

        return toDTO(animalRepository.save(animal));
    }

    @Transactional(readOnly = true)
    public List<AnimalDTO> listar(Long idClinicaFiltro) {
        AuthContext ctx = AuthContextResolver.get();

        if (!ctx.isAdmin()) {
            return animalRepository.findByTutor_Clinica_IdClinica(ctx.idClinica())
                    .stream().map(this::toDTO).toList();
        }

        if (idClinicaFiltro != null) {
            clinicaService.buscarEntidadePorId(idClinicaFiltro);
            return animalRepository.findByTutor_Clinica_IdClinica(idClinicaFiltro)
                    .stream().map(this::toDTO).toList();
        }
        return animalRepository.findAll().stream().map(this::toDTO).toList();
    }

    @Transactional
    public AnimalDTO atualizar(Long id, AnimalDTO dto) {
        AuthContext ctx = AuthContextResolver.get();
        Animal animal = buscarEntidadePorId(id);

        if (!ctx.isAdmin() && !ctx.pertenceAClinica(animal.getTutor().getClinica().getIdClinica())) {
            throw new AccessDeniedException("Acesso negado: animal pertence a outra clínica");
        }

        Tutor tutor = tutorService.buscarEntidadePorId(dto.idTutor());

        if (!ctx.isAdmin() && !ctx.pertenceAClinica(tutor.getClinica().getIdClinica())) {
            throw new AccessDeniedException("Acesso negado: tutor destino pertence a outra clínica");
        }

        animal.setTutor(tutor);
        animal.setNome(dto.nome());
        animal.setEspecie(dto.especie());
        animal.setRaca(dto.raca());
        animal.setIdade(dto.idade());
        animal.setPeso(dto.peso());
        animal.setSexo(dto.sexo());

        return toDTO(animalRepository.save(animal));
    }

    @Transactional
    public void deletar(Long id) {
        AuthContext ctx = AuthContextResolver.get();
        Animal animal = buscarEntidadePorId(id);

        if (!ctx.isAdmin() && !ctx.pertenceAClinica(animal.getTutor().getClinica().getIdClinica())) {
            throw new AccessDeniedException("Acesso negado: animal pertence a outra clínica");
        }

        animalRepository.delete(animal);
    }

    public Animal buscarEntidadePorId(Long id) {
        return animalRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Animal não encontrado com id " + id));
    }

    private AnimalDTO toDTO(Animal animal) {
        return new AnimalDTO(
                animal.getIdAnimal(),
                animal.getTutor().getIdTutor(),
                animal.getNome(),
                animal.getEspecie(),
                animal.getRaca(),
                animal.getIdade(),
                animal.getPeso(),
                animal.getSexo()
        );
    }
}