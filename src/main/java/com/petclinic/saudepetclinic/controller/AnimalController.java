package com.petclinic.saudepetclinic.controller;

import com.petclinic.saudepetclinic.dto.AnimalDTO;
import com.petclinic.saudepetclinic.model.Animal;
import com.petclinic.saudepetclinic.service.AnimalService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/animais")
public class AnimalController {

    private final AnimalService service;

    public AnimalController(AnimalService service) {
        this.service = service;
    }

    @PostMapping
    public Animal salvar(@RequestBody AnimalDTO dto) {
        return service.salvar(dto);
    }

    @GetMapping
    public List<Animal> listar() {
        return service.listar();
    }

}