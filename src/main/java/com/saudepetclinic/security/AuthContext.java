package com.saudepetclinic.security;

public record AuthContext(String perfil, Long idClinica, Long idUsuario) {

    public boolean isAdmin() {
        return "ADMIN".equals(perfil);
    }

    public boolean isGestor() {
        return "GESTOR".equals(perfil);
    }

    public boolean pertenceAClinica(Long idClinicaAlvo) {
        if (isAdmin()) return true;
        return idClinica != null && idClinica.equals(idClinicaAlvo);
    }
}