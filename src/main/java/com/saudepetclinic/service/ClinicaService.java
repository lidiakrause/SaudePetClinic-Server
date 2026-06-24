package com.saudepetclinic.service;

import com.saudepetclinic.dto.ClinicaDTO;
import com.saudepetclinic.exception.BusinessException;
import com.saudepetclinic.exception.ResourceNotFoundException;
import com.saudepetclinic.model.Clinica;
import com.saudepetclinic.repository.ClinicaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ClinicaService {

    private final ClinicaRepository clinicaRepository;

    @Transactional
    public ClinicaDTO criar(ClinicaDTO dto) {
        if (clinicaRepository.findByCnpj(dto.cnpj()).isPresent()) {
            throw new BusinessException("Já existe uma clínica cadastrada com o CNPJ " + dto.cnpj());
        }

        Clinica clinica = new Clinica();
        clinica.setCnpj(dto.cnpj());
        clinica.setRazaoSocial(dto.razaoSocial());
        clinica.setNomeFantasia(dto.nomeFantasia());

        return toDTO(clinicaRepository.save(clinica));
    }

    @Transactional(readOnly = true)
    public List<ClinicaDTO> listarTodas() {
        return clinicaRepository.findAll().stream().map(this::toDTO).toList();
    }

    @Transactional(readOnly = true)
    public ClinicaDTO buscarPorId(Long id) {
        return toDTO(buscarEntidadePorId(id));
    }

    @Transactional
    public ClinicaDTO atualizar(Long id, ClinicaDTO dto) {
        Clinica clinica = buscarEntidadePorId(id);

        clinicaRepository.findByCnpj(dto.cnpj())
                .filter(outra -> !outra.getIdClinica().equals(id))
                .ifPresent(outra -> {
                    throw new BusinessException("Já existe outra clínica cadastrada com o CNPJ " + dto.cnpj());
                });

        clinica.setCnpj(dto.cnpj());
        clinica.setRazaoSocial(dto.razaoSocial());
        clinica.setNomeFantasia(dto.nomeFantasia());

        return toDTO(clinicaRepository.save(clinica));
    }

    @Transactional
    public void deletar(Long id) {
        Clinica clinica = buscarEntidadePorId(id);
        clinicaRepository.delete(clinica);
    }

    @Transactional(readOnly = true)
    public Clinica buscarEntidadePorId(Long id) {
        return clinicaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Clínica não encontrada com id " + id));
    }

    private ClinicaDTO toDTO(Clinica clinica) {
        return new ClinicaDTO(
                clinica.getIdClinica(),
                clinica.getCnpj(),
                clinica.getRazaoSocial(),
                clinica.getNomeFantasia()
        );
    }
}
