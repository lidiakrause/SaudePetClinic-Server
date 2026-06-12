package com.petclinic.saudepetclinic.controller;

import com.petclinic.saudepetclinic.dto.AgendamentoDTO;
import com.petclinic.saudepetclinic.model.Agendamento;
import com.petclinic.saudepetclinic.service.AgendamentoService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/agendamentos")
public class AgendamentoController {

    private final AgendamentoService service;

    public AgendamentoController(AgendamentoService service) {
        this.service = service;
    }

    @PostMapping
    public Agendamento salvar(@RequestBody AgendamentoDTO dto) {
        return service.salvar(dto);
    }

    @GetMapping
    public List<Agendamento> listar() {
        return service.listar();
    }
}