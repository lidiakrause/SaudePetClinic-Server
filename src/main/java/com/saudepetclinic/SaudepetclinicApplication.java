package com.saudepetclinic;

import io.github.cdimascio.dotenv.Dotenv;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@OpenAPIDefinition(
        info = @Info(
                title = "SaudePetClinic API",
                version = "1.0",
                description = "API para gestão de clínicas veterinárias: clínicas, usuários, "
                        + "veterinários, agenda de atendimento, tutores, animais e agendamentos. "
                        + "Todas as rotas (exceto POST /api/auth/login) exigem token JWT. "
                        + "Clique em 'Authorize' e informe 'Bearer {token}'.",
                contact = @Contact(name = "SaudePetClinic")
        )
)
@SecurityScheme(
        name = "bearerAuth",
        type = SecuritySchemeType.HTTP,
        scheme = "bearer",
        bearerFormat = "JWT",
        description = "Token JWT obtido em POST /api/auth/login (CPF + senha)."
)
@SpringBootApplication
public class SaudepetclinicApplication {

    public static void main(String[] args) {
        Dotenv dotenv = Dotenv.configure().load();
        dotenv.entries().forEach(entry ->
                System.setProperty(entry.getKey(), entry.getValue())
        );

        Process cloudflareProcess = null;

        try {
            System.out.println("[Cloudflare] Iniciando...");
            String token = System.getProperty("CLOUDFLARE_TOKEN");

            cloudflareProcess = new ProcessBuilder("cloudflared", "tunnel", "run", "--token", token).start();

            System.out.println("[Cloudflare] Iniciado com Sucesso!");
        } catch (Exception e) {
            System.err.println("[Cloudflare] ERRO ao iniciar: " + e.getMessage());
        }

        if (cloudflareProcess != null) {
            final Process processToDestroy = cloudflareProcess;
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                System.out.println("[Cloudflare] Encerrando o túnel...");
                processToDestroy.destroy();
            }));
        }

        SpringApplication.run(SaudepetclinicApplication.class, args);
    }
}