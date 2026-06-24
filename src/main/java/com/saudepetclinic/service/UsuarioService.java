package com.saudepetclinic.service;

import com.saudepetclinic.dto.UsuarioRequestDTO;
import com.saudepetclinic.dto.UsuarioResponseDTO;
import com.saudepetclinic.dto.UsuarioUpdateDTO;
import com.saudepetclinic.exception.BusinessException;
import com.saudepetclinic.exception.ResourceNotFoundException;
import com.saudepetclinic.model.Clinica;
import com.saudepetclinic.model.Perfil;
import com.saudepetclinic.model.Usuario;
import com.saudepetclinic.repository.UsuarioRepository;
import com.saudepetclinic.security.AuthContext;
import com.saudepetclinic.security.AuthContextResolver;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final ClinicaService clinicaService;
    private final AgendaConfigVeterinarioService agendaConfigService;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public UsuarioResponseDTO criar(UsuarioRequestDTO dto) {
        AuthContext ctx = AuthContextResolver.get();

        if (ctx.isGestor()) {
            garantirMesmaClinica(dto.idClinica(), ctx);
            if (dto.perfil() == Perfil.ADMIN || dto.perfil() == Perfil.GESTOR) {
                throw new AccessDeniedException("GESTOR não pode criar usuários com perfil ADMIN ou GESTOR");
            }
        }

        if (usuarioRepository.findByCpf(dto.cpf()).isPresent()) {
            throw new BusinessException("Já existe um usuário cadastrado com o CPF " + dto.cpf());
        }

        Clinica clinica = resolverClinica(dto.perfil(), dto.idClinica());

        Usuario usuario = new Usuario();
        usuario.setClinica(clinica);
        usuario.setCpf(dto.cpf());
        usuario.setSenha(passwordEncoder.encode(dto.senha()));
        usuario.setNome(dto.nome());
        usuario.setPerfil(dto.perfil());

        validarECompletarDadosVeterinario(usuario, dto.perfil(), dto.crmv());
        usuario.setTelefone(dto.perfil() == Perfil.VETERINARIO ? dto.telefone() : null);

        usuario = usuarioRepository.save(usuario);

        sincronizarAgenda(usuario, dto.perfil(), dto.horarioInicio(), dto.horarioFim(),
                dto.atendeSabado(), dto.atendeDomingo());

        return toDTO(usuario);
    }

    @Transactional(readOnly = true)
    public List<UsuarioResponseDTO> listar(Long idClinicaFiltro) {
        AuthContext ctx = AuthContextResolver.get();

        if (!ctx.isAdmin()) {
            return usuarioRepository.findByClinica_IdClinica(ctx.idClinica())
                    .stream().map(this::toDTO).toList();
        }

        if (idClinicaFiltro != null) {
            clinicaService.buscarEntidadePorId(idClinicaFiltro);
            return usuarioRepository.findByClinica_IdClinica(idClinicaFiltro)
                    .stream().map(this::toDTO).toList();
        }
        return usuarioRepository.findAll().stream().map(this::toDTO).toList();
    }

    @Transactional(readOnly = true)
    public UsuarioResponseDTO buscarPorId(Long id) {
        AuthContext ctx = AuthContextResolver.get();
        Usuario usuario = buscarEntidadePorId(id);

        if (!ctx.isAdmin()) {
            garantirMesmaClinica(
                    usuario.getClinica() != null ? usuario.getClinica().getIdClinica() : null, ctx);
        }
        return toDTO(usuario);
    }

    @Transactional
    public UsuarioResponseDTO atualizar(Long id, UsuarioUpdateDTO dto) {
        AuthContext ctx = AuthContextResolver.get();
        Usuario usuario = buscarEntidadePorId(id);

        if (ctx.isGestor()) {
            garantirMesmaClinica(
                    usuario.getClinica() != null ? usuario.getClinica().getIdClinica() : null, ctx);
            garantirMesmaClinica(dto.idClinica(), ctx);
            if (dto.perfil() == Perfil.ADMIN || dto.perfil() == Perfil.GESTOR) {
                throw new AccessDeniedException("GESTOR não pode promover usuários para ADMIN ou GESTOR");
            }
        }

        usuarioRepository.findByCpf(dto.cpf())
                .filter(outro -> !outro.getIdUsuario().equals(id))
                .ifPresent(outro -> {
                    throw new BusinessException("Já existe outro usuário cadastrado com o CPF " + dto.cpf());
                });

        Clinica clinica = resolverClinica(dto.perfil(), dto.idClinica());
        usuario.setClinica(clinica);
        usuario.setCpf(dto.cpf());
        usuario.setNome(dto.nome());
        usuario.setPerfil(dto.perfil());

        validarECompletarDadosVeterinario(usuario, dto.perfil(), dto.crmv());
        usuario.setTelefone(dto.perfil() == Perfil.VETERINARIO ? dto.telefone() : null);

        if (dto.senha() != null && !dto.senha().isBlank()) {
            usuario.setSenha(passwordEncoder.encode(dto.senha()));
        }

        usuario = usuarioRepository.save(usuario);

        sincronizarAgenda(usuario, dto.perfil(), dto.horarioInicio(), dto.horarioFim(),
                dto.atendeSabado(), dto.atendeDomingo());

        return toDTO(usuario);
    }

    @Transactional
    public void deletar(Long id) {
        AuthContext ctx = AuthContextResolver.get();
        Usuario usuario = buscarEntidadePorId(id);

        if (ctx.isGestor()) {
            garantirMesmaClinica(
                    usuario.getClinica() != null ? usuario.getClinica().getIdClinica() : null, ctx);
        }

        usuarioRepository.delete(usuario);
    }

    public Usuario buscarEntidadePorId(Long id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado com id " + id));
    }


    private void garantirMesmaClinica(Long idClinicaAlvo, AuthContext ctx) {
        if (!ctx.pertenceAClinica(idClinicaAlvo)) {
            throw new AccessDeniedException("Acesso negado: operação fora da sua clínica");
        }
    }

    private Clinica resolverClinica(Perfil perfil, Long idClinica) {
        if (perfil == Perfil.ADMIN) {
            if (idClinica != null) {
                throw new BusinessException("Usuários com perfil ADMIN não devem ser vinculados a uma clínica");
            }
            return null;
        }
        if (idClinica == null) {
            throw new BusinessException("idClinica é obrigatório para perfil " + perfil);
        }
        return clinicaService.buscarEntidadePorId(idClinica);
    }

    private void validarECompletarDadosVeterinario(Usuario usuario, Perfil perfil, String crmvInformado) {
        if (perfil == Perfil.VETERINARIO) {
            if (crmvInformado == null || crmvInformado.isBlank()) {
                throw new BusinessException("crmv é obrigatório para perfil VETERINARIO");
            }
            usuarioRepository.findByCrmv(crmvInformado)
                    .filter(outro -> usuario.getIdUsuario() == null
                            || !outro.getIdUsuario().equals(usuario.getIdUsuario()))
                    .ifPresent(outro -> {
                        throw new BusinessException("Já existe um veterinário cadastrado com o CRMV " + crmvInformado);
                    });
            usuario.setCrmv(crmvInformado);
        } else {
            usuario.setCrmv(null);
        }
    }

    private void sincronizarAgenda(Usuario usuario, Perfil perfil, LocalTime horarioInicio, LocalTime horarioFim,
                                   Boolean atendeSabado, Boolean atendeDomingo) {
        if (perfil != Perfil.VETERINARIO) {
            agendaConfigService.sincronizar(usuario, null, null, false, false);
            return;
        }

        if (horarioInicio == null || horarioFim == null) {
            throw new BusinessException("horarioInicio e horarioFim são obrigatórios para perfil VETERINARIO");
        }

        agendaConfigService.sincronizar(
                usuario,
                horarioInicio,
                horarioFim,
                Boolean.TRUE.equals(atendeSabado),
                Boolean.TRUE.equals(atendeDomingo));
    }

    private UsuarioResponseDTO toDTO(Usuario u) {
        AgendaConfigVeterinarioService.ResumoAgenda resumo = u.getPerfil() == Perfil.VETERINARIO
                ? agendaConfigService.obterResumo(u.getIdUsuario())
                : new AgendaConfigVeterinarioService.ResumoAgenda(null, null, false, false);

        return new UsuarioResponseDTO(
                u.getIdUsuario(),
                u.getClinica() != null ? u.getClinica().getIdClinica() : null,
                u.getClinica() != null ? u.getClinica().getNomeFantasia() : null,
                u.getCpf(),
                u.getNome(),
                u.getPerfil(),
                u.getCrmv(),
                u.getTelefone(),
                resumo.horarioInicio(),
                resumo.horarioFim(),
                resumo.atendeSabado(),
                resumo.atendeDomingo()
        );
    }
}