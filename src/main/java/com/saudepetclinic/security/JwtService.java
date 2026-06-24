package com.saudepetclinic.security;

import com.saudepetclinic.model.Usuario;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
public class JwtService {

    private static final String CLAIM_PERFIL = "perfil";

    @Value("${app.jwt.secret}")
    private String secret;

    @Value("${app.jwt.expiration-ms}")
    private long expirationMs;

    private SecretKey signingKey;

    @PostConstruct
    void init() {
        signingKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    public String gerarToken(Usuario usuario) {
        Date agora = new Date();

        return Jwts.builder()
                .setSubject(usuario.getCpf())
                .claim("idUsuario", usuario.getIdUsuario())
                .claim("nome", usuario.getNome())
                .claim(CLAIM_PERFIL, usuario.getPerfil().name())
                .claim("idClinica",
                        usuario.getClinica() != null
                                ? usuario.getClinica().getIdClinica()
                                : null)
                .setIssuedAt(agora)
                .setExpiration(new Date(agora.getTime() + expirationMs))
                .signWith(signingKey)
                .compact();
    }

    public Claims extrairClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(signingKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public boolean tokenValido(String token) {
        try {
            extrairClaims(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }
}