package com.saudepetclinic.service;

import com.saudepetclinic.dto.TutorDTO;
import com.saudepetclinic.exception.BusinessException;
import com.saudepetclinic.exception.ResourceNotFoundException;
import com.saudepetclinic.model.Clinica;
import com.saudepetclinic.model.Tutor;
import com.saudepetclinic.repository.TutorRepository;
import com.saudepetclinic.security.AuthContext;
import com.saudepetclinic.security.AuthContextResolver;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TutorService {

    private final TutorRepository tutorRepository;
    private final ClinicaService clinicaService;

    @Transactional
    public TutorDTO criar(TutorDTO dto) {
        AuthContext ctx = AuthContextResolver.get();

        Long idClinica = ctx.isAdmin() ? dto.idClinica() : ctx.idClinica();

        Clinica clinica = clinicaService.buscarEntidadePorId(idClinica);

        tutorRepository.findByCpfAndClinica_IdClinica(dto.cpf(), idClinica).ifPresent(t -> {
            throw new BusinessException("Já existe um tutor com o CPF " + dto.cpf() + " nesta clínica");
        });

        Tutor tutor = new Tutor();
        tutor.setClinica(clinica);
        tutor.setNome(dto.nome());
        tutor.setCpf(dto.cpf());
        tutor.setTelefone(dto.telefone());
        tutor.setEndereco(dto.endereco());

        return toDTO(tutorRepository.save(tutor));
    }

    @Transactional(readOnly = true)
    public List<TutorDTO> listar(Long idClinicaFiltro) {
        AuthContext ctx = AuthContextResolver.get();

        if (!ctx.isAdmin()) {
            return tutorRepository.findByClinica_IdClinica(ctx.idClinica())
                    .stream().map(this::toDTO).toList();
        }

        if (idClinicaFiltro != null) {
            clinicaService.buscarEntidadePorId(idClinicaFiltro);
            return tutorRepository.findByClinica_IdClinica(idClinicaFiltro)
                    .stream().map(this::toDTO).toList();
        }
        return tutorRepository.findAll().stream().map(this::toDTO).toList();
    }

    @Transactional(readOnly = true)
    public TutorDTO buscarPorId(Long id) {
        AuthContext ctx = AuthContextResolver.get();
        Tutor tutor = buscarEntidadePorId(id);

        if (!ctx.isAdmin() && !ctx.pertenceAClinica(tutor.getClinica().getIdClinica())) {
            throw new AccessDeniedException("Acesso negado: tutor pertence a outra clínica");
        }
        return toDTO(tutor);
    }

    @Transactional
    public TutorDTO atualizar(Long id, TutorDTO dto) {
        AuthContext ctx = AuthContextResolver.get();
        Tutor tutor = buscarEntidadePorId(id);

        if (!ctx.isAdmin() && !ctx.pertenceAClinica(tutor.getClinica().getIdClinica())) {
            throw new AccessDeniedException("Acesso negado: tutor pertence a outra clínica");
        }

        Long idClinica = ctx.isAdmin() ? dto.idClinica() : ctx.idClinica();
        Clinica clinica = clinicaService.buscarEntidadePorId(idClinica);

        tutorRepository.findByCpfAndClinica_IdClinica(dto.cpf(), idClinica)
                .filter(outro -> !outro.getIdTutor().equals(id))
                .ifPresent(outro -> {
                    throw new BusinessException("Já existe outro tutor com o CPF " + dto.cpf() + " nesta clínica");
                });

        tutor.setClinica(clinica);
        tutor.setNome(dto.nome());
        tutor.setCpf(dto.cpf());
        tutor.setTelefone(dto.telefone());
        tutor.setEndereco(dto.endereco());

        return toDTO(tutorRepository.save(tutor));
    }

    @Transactional
    public void deletar(Long id) {
        AuthContext ctx = AuthContextResolver.get();
        Tutor tutor = buscarEntidadePorId(id);

        if (!ctx.isAdmin() && !ctx.pertenceAClinica(tutor.getClinica().getIdClinica())) {
            throw new AccessDeniedException("Acesso negado: tutor pertence a outra clínica");
        }

        tutorRepository.delete(tutor);
    }

    public Tutor buscarEntidadePorId(Long id) {
        return tutorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tutor não encontrado com id " + id));
    }

    private TutorDTO toDTO(Tutor tutor) {
        return new TutorDTO(
                tutor.getIdTutor(),
                tutor.getClinica().getIdClinica(),
                tutor.getNome(),
                tutor.getCpf(),
                tutor.getTelefone(),
                tutor.getEndereco()
        );
    }
}
