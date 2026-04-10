package com.petclinic.saudepetclinic.controller;

import com.petclinic.saudepetclinic.dto.CadastroDTO;
import com.petclinic.saudepetclinic.model.Animal;
import com.petclinic.saudepetclinic.model.Tutor;
import com.petclinic.saudepetclinic.repository.AnimalRepository;
import com.petclinic.saudepetclinic.repository.TutorRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/cadastro")
@CrossOrigin("*")
public class CadastroController {

    private final TutorRepository tutorRepo;
    private final AnimalRepository animalRepo;

    public CadastroController(TutorRepository tutorRepo, AnimalRepository animalRepo) {
        this.tutorRepo = tutorRepo;
        this.animalRepo = animalRepo;
    }

    @PostMapping
    public String cadastrar(@RequestBody CadastroDTO dto) {

        Tutor tutor = new Tutor();
        tutor.setNome(dto.nome);
        tutor.setCpf(dto.cpf);
        tutor.setTelefone(dto.telefone);
        tutor.setEndereco(dto.endereco);
        tutor.setNumero(dto.numero);
        tutor.setCep(dto.cep);

        tutor = tutorRepo.save(tutor);

        Animal animal = new Animal();
        animal.setNome(dto.nomeAnimal);
        animal.setEspecie(dto.especie);
        animal.setRaca(dto.raca);
        animal.setIdade(dto.idade);
        animal.setSexo(dto.sexo);
        animal.setPeso(dto.peso);
        animal.setTutor(tutor);

        animalRepo.save(animal);

        return "Cadastro realizado com sucesso!";
    }

    @GetMapping
    public List<Tutor> listar() {
        return tutorRepo.findAll();
    }
}