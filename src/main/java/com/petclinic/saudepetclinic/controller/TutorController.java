package com.petclinic.saudepetclinic.controller;

import com.petclinic.saudepetclinic.dto.TutorDTO;
import com.petclinic.saudepetclinic.model.Tutor;
import com.petclinic.saudepetclinic.service.TutorService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/tutores")
public class TutorController {

    private final TutorService service;

    public TutorController(TutorService service) {
        this.service = service;
    }

    @PostMapping
    public Tutor salvar(@RequestBody TutorDTO dto) {
        return service.salvar(dto);
    }

    @GetMapping
    public List<Tutor> listar() {
        return service.listar();
    }
}