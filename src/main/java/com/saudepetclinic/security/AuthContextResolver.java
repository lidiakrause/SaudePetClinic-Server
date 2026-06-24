package com.saudepetclinic.security;

import io.jsonwebtoken.Claims;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public final class AuthContextResolver {

    private AuthContextResolver() {
    }

    public static AuthContext get() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            throw new IllegalStateException("Nenhum usuário autenticado no contexto de segurança");
        }

        String perfil = auth.getAuthorities().stream()
                .findFirst()
                .map(a -> a.getAuthority())
                .orElse("");

        Long idClinica = null;
        Long idUsuario = null;

        if (auth.getDetails() instanceof Claims claims) {
            Object idClinicaClaim = claims.get("idClinica");
            if (idClinicaClaim instanceof Number n) {
                idClinica = n.longValue();
            }
            Object idUsuarioClaim = claims.get("idUsuario");
            if (idUsuarioClaim instanceof Number n) {
                idUsuario = n.longValue();
            }
        }

        return new AuthContext(perfil, idClinica, idUsuario);
    }
}
