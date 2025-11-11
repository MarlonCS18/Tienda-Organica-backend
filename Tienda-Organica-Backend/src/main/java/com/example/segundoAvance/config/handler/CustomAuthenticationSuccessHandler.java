package com.example.segundoAvance.config.handler;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

// ... (imports)

@Component
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        
        // Obtenemos la autoridad (rol)
        var authorities = authentication.getAuthorities();
        
        // Buscamos si tiene el rol ADMIN
        boolean isAdmin = authorities.stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        if (isAdmin) {
            // Si es ADMIN, lo mandamos al dashboard
            response.sendRedirect("/admin/dashboard");
        } else {
            // Si es cualquier otra cosa (ej: ROLE_USER),
            // lo mandamos a la raíz (que lo redirigirá a /login de nuevo)
            // O, mejor, a un error de "acceso denegado" si quisieras.
            // Por ahora, con /login está bien para evitar errores.
            response.sendRedirect("/login?error=true");
        }
    }
}