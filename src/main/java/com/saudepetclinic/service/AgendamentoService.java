package com.saudepetclinic.service;

import com.saudepetclinic.dto.AgendamentoRequestDTO;
import com.saudepetclinic.dto.AgendamentoResponseDTO;
import com.saudepetclinic.exception.BusinessException;
import com.saudepetclinic.exception.ResourceNotFoundException;
import com.saudepetclinic.model.*;
import com.saudepetclinic.repository.AgendaConfigVeterinarioRepository;
import com.saudepetclinic.repository.AgendamentoRepository;
import com.saudepetclinic.security.AuthContext;
import com.saudepetclinic.security.AuthContextResolver;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AgendamentoService {

    private final AgendamentoRepository agendamentoRepository;
    private final AgendaConfigVeterinarioRepository agendaConfigRepository;
    private final AnimalService animalService;
    private final UsuarioService usuarioService;

    @Transactional
    public AgendamentoResponseDTO criar(AgendamentoRequestDTO dto) {
        AuthContext ctx = AuthContextResolver.get();

        Animal animal = animalService.buscarEntidadePorId(dto.idAnimal());
        Usuario veterinario = buscarVeterinario(dto.idVet());

        verificarEscopoClinica(animal, veterinario, ctx);
        validarExpediente(veterinario, dto.data(), dto.hora());
        validarConflito(dto.idVet(), dto.idAnimal(), dto.data(), dto.hora(), null);

        Agendamento agendamento = new Agendamento();
        agendamento.setAnimal(animal);
        agendamento.setVeterinario(veterinario);
        agendamento.setData(dto.data());
        agendamento.setHora(dto.hora());
        agendamento.setStatus(StatusAgendamento.AGENDADO);
        agendamento.setAnotacoes(dto.anotacoes());

        return toDTO(agendamentoRepository.save(agendamento));
    }

    @Transactional(readOnly = true)
    public List<AgendamentoResponseDTO> listar(Long idAnimal, Long idVet, LocalDate data) {
        AuthContext ctx = AuthContextResolver.get();

        if (idAnimal != null) {
            verificarEscopoAnimal(animalService.buscarEntidadePorId(idAnimal), ctx);
            return agendamentoRepository.findByAnimal_IdAnimal(idAnimal)
                    .stream().map(this::toDTO).toList();
        }

        if (idVet != null && data != null) {
            verificarEscopoVeterinario(buscarVeterinario(idVet), ctx);
            return agendamentoRepository.findByVeterinario_IdUsuarioAndData(idVet, data)
                    .stream().map(this::toDTO).toList();
        }

        if (ctx.isAdmin()) {
            return agendamentoRepository.findAll().stream().map(this::toDTO).toList();
        }
        return agendamentoRepository.findByVeterinario_Clinica_IdClinica(ctx.idClinica())
                .stream().map(this::toDTO).toList();
    }

    @Transactional
    public AgendamentoResponseDTO atualizar(Long id, AgendamentoRequestDTO dto) {
        AuthContext ctx = AuthContextResolver.get();
        Agendamento agendamento = buscarEntidadePorId(id);

        verificarEscopoVeterinario(agendamento.getVeterinario(), ctx);

        if (agendamento.getStatus() != StatusAgendamento.AGENDADO) {
            throw new BusinessException(
                    "Só é possível reagendar um agendamento com status AGENDADO. Status atual: "
                            + agendamento.getStatus());
        }

        Animal animal = animalService.buscarEntidadePorId(dto.idAnimal());
        Usuario veterinario = buscarVeterinario(dto.idVet());

        verificarEscopoClinica(animal, veterinario, ctx);
        validarExpediente(veterinario, dto.data(), dto.hora());
        validarConflito(dto.idVet(), dto.idAnimal(), dto.data(), dto.hora(), id);

        agendamento.setAnimal(animal);
        agendamento.setVeterinario(veterinario);
        agendamento.setData(dto.data());
        agendamento.setHora(dto.hora());
        agendamento.setAnotacoes(dto.anotacoes());

        return toDTO(agendamentoRepository.save(agendamento));
    }

    @Transactional
    public AgendamentoResponseDTO atualizarStatus(Long id, StatusAgendamento novoStatus, String anotacoes) {
        AuthContext ctx = AuthContextResolver.get();
        Agendamento agendamento = buscarEntidadePorId(id);

        verificarEscopoVeterinario(agendamento.getVeterinario(), ctx);

        if (novoStatus == StatusAgendamento.AGENDADO) {
            throw new BusinessException("Não é possível reverter um agendamento para o status AGENDADO.");
        }
        if (agendamento.getStatus() != StatusAgendamento.AGENDADO) {
            throw new BusinessException(
                    "Agendamento já está " + agendamento.getStatus() + " e não pode ser alterado.");
        }
        if (anotacoes == null || anotacoes.isBlank()) {
            throw new BusinessException("anotacoes é obrigatório ao definir o status como " + novoStatus);
        }

        agendamento.setStatus(novoStatus);
        agendamento.setAnotacoes(anotacoes);
        return toDTO(agendamentoRepository.save(agendamento));
    }

    public Agendamento buscarEntidadePorId(Long id) {
        return agendamentoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Agendamento não encontrado com id " + id));
    }

    private Usuario buscarVeterinario(Long idUsuario) {
        Usuario usuario = usuarioService.buscarEntidadePorId(idUsuario);
        if (usuario.getPerfil() != Perfil.VETERINARIO) {
            throw new BusinessException(
                    "Usuário " + idUsuario + " não possui perfil VETERINARIO e não pode receber agendamentos");
        }
        return usuario;
    }

    private void verificarEscopoClinica(Animal animal, Usuario veterinario, AuthContext ctx) {
        verificarEscopoAnimal(animal, ctx);
        verificarEscopoVeterinario(veterinario, ctx);
    }

    private void verificarEscopoAnimal(Animal animal, AuthContext ctx) {
        if (!ctx.pertenceAClinica(animal.getTutor().getClinica().getIdClinica())) {
            throw new AccessDeniedException("Acesso negado: animal pertence a outra clínica");
        }
    }

    private void verificarEscopoVeterinario(Usuario vet, AuthContext ctx) {
        Long idClinicaVet = vet.getClinica() != null ? vet.getClinica().getIdClinica() : null;
        if (!ctx.pertenceAClinica(idClinicaVet)) {
            throw new AccessDeniedException("Acesso negado: veterinário pertence a outra clínica");
        }
    }

    private void validarExpediente(Usuario veterinario, LocalDate data, LocalTime hora) {
        int diaSemana = data.getDayOfWeek().getValue();
        boolean dentroDoExpediente = agendaConfigRepository
                .findByUsuario_IdUsuarioAndDiaSemana(veterinario.getIdUsuario(), diaSemana)
                .stream()
                .anyMatch(t -> !hora.isBefore(t.getHoraInicio()) && hora.isBefore(t.getHoraFim()));

        if (!dentroDoExpediente) {
            throw new BusinessException("Horário fora do expediente do veterinário para este dia da semana.");
        }
    }

    private void validarConflito(Long idVet, Long idAnimal, LocalDate data, LocalTime hora, Long idIgnorar) {
        boolean conflitoVet = agendamentoRepository
                .findByVeterinario_IdUsuarioAndDataAndHoraAndStatusNot(idVet, data, hora, StatusAgendamento.CANCELADO)
                .stream().anyMatch(a -> !a.getIdAgendamento().equals(idIgnorar));
        if (conflitoVet) {
            throw new BusinessException("Veterinário já possui agendamento ativo neste horário.");
        }

        boolean conflitoAnimal = agendamentoRepository
                .findByAnimal_IdAnimalAndDataAndHoraAndStatusNot(idAnimal, data, hora, StatusAgendamento.CANCELADO)
                .stream().anyMatch(a -> !a.getIdAgendamento().equals(idIgnorar));
        if (conflitoAnimal) {
            throw new BusinessException("Animal já possui agendamento ativo neste horário.");
        }
    }

    private AgendamentoResponseDTO toDTO(Agendamento a) {
        return new AgendamentoResponseDTO(
                a.getIdAgendamento(),
                a.getAnimal().getIdAnimal(),
                a.getAnimal().getNome(),
                a.getVeterinario().getIdUsuario(),
                a.getVeterinario().getNome(),
                a.getData(),
                a.getHora(),
                a.getStatus(),
                a.getAnotacoes()
        );
    }
}