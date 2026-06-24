package com.saudepetclinic.service;

import com.saudepetclinic.exception.BusinessException;
import com.saudepetclinic.model.AgendaConfigVeterinario;
import com.saudepetclinic.model.Usuario;
import com.saudepetclinic.repository.AgendaConfigVeterinarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AgendaConfigVeterinarioService {

    private final AgendaConfigVeterinarioRepository agendaConfigRepository;

    @Transactional
    public void sincronizar(Usuario veterinario, LocalTime horarioInicio, LocalTime horarioFim,
                            boolean atendeSabado, boolean atendeDomingo) {

        agendaConfigRepository.deleteAll(
                agendaConfigRepository.findByUsuario_IdUsuario(veterinario.getIdUsuario()));

        if (horarioInicio == null || horarioFim == null) {
            return;
        }

        if (!horarioInicio.isBefore(horarioFim)) {
            throw new BusinessException("horarioInicio deve ser anterior a horarioFim");
        }

        List<Integer> dias = new ArrayList<>(List.of(1, 2, 3, 4, 5));
        if (atendeSabado) dias.add(6);
        if (atendeDomingo) dias.add(7);

        for (Integer dia : dias) {
            AgendaConfigVeterinario config = new AgendaConfigVeterinario();
            config.setUsuario(veterinario);
            config.setDiaSemana(dia);
            config.setHoraInicio(horarioInicio);
            config.setHoraFim(horarioFim);
            agendaConfigRepository.save(config);
        }
    }

    @Transactional(readOnly = true)
    public ResumoAgenda obterResumo(Long idUsuario) {
        List<AgendaConfigVeterinario> turnos = agendaConfigRepository.findByUsuario_IdUsuario(idUsuario);

        if (turnos.isEmpty()) {
            return new ResumoAgenda(null, null, false, false);
        }

        LocalTime horaInicio = turnos.get(0).getHoraInicio();
        LocalTime horaFim = turnos.get(0).getHoraFim();
        boolean atendeSabado = turnos.stream().anyMatch(t -> t.getDiaSemana() == 6);
        boolean atendeDomingo = turnos.stream().anyMatch(t -> t.getDiaSemana() == 7);

        return new ResumoAgenda(horaInicio, horaFim, atendeSabado, atendeDomingo);
    }

    public record ResumoAgenda(LocalTime horarioInicio, LocalTime horarioFim,
                               boolean atendeSabado, boolean atendeDomingo) {
    }
}