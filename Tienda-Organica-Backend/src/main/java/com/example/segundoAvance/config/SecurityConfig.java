package com.example.segundoAvance.config;

import com.example.segundoAvance.config.handler.CustomAuthenticationSuccessHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Autowired
    private CustomAuthenticationSuccessHandler successHandler;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(authorize -> authorize
                
                // --- INICIO DE LA SECCIÓN MODIFICADA ---

                // 1. Damos permiso público a la API que acabamos de crear (/api/v1/**)
                // 2. Damos permiso público a la página de Login (/login)
                // 3. Damos permiso público al CSS para que el login se vea bien (/css/**)
                .requestMatchers("/api/v1/**", "/login", "/css/**").permitAll()
                
                // --- FIN DE LA SECCIÓN MODIFICADA ---

                // 4. Protegemos el panel de admin
                .requestMatchers("/admin/**").hasRole("ADMIN")
                
                // 5. CUALQUIER otra ruta (como "/") debe estar autenticada.
                //    Esto es lo que te enviará a /login automáticamente.
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage("/login") // Le decimos cuál es tu página de login
                .successHandler(successHandler) // Usa tu manejador de redirección
                .permitAll()
            )
            .logout(logout -> logout
                .logoutSuccessUrl("/") // Al cerrar sesión, te mandará a /login
                .permitAll()
            );

        return http.build();
    }
}