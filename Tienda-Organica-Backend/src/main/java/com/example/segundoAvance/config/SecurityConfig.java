package com.example.segundoAvance.config;

import com.example.segundoAvance.config.handler.CustomAuthenticationSuccessHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

// --- ¡NUEVAS IMPORTACIONES NECESARIAS! ---
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import java.util.Arrays;

@Configuration
public class SecurityConfig {

    @Autowired
    private CustomAuthenticationSuccessHandler successHandler;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // --- ¡NUEVO BEAN DE CORS! ---
    // Esto le dice a Spring que permita peticiones desde tu frontend
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        // Permite peticiones SOLO desde tu frontend
        configuration.setAllowedOrigins(Arrays.asList("http://localhost:3000")); 
        // Permite los métodos HTTP comunes
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        // Permite cabeceras comunes
        configuration.setAllowedHeaders(Arrays.asList("Authorization", "Cache-Control", "Content-Type"));
        // Permite que se envíen cookies (si usas autenticación basada en sesión)
        configuration.setAllowCredentials(true); 
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        // Aplica esta configuración a TODAS las rutas de tu API (incluyendo /img/**)
        source.registerCorsConfiguration("/**", configuration); 
        return source;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            // --- ¡CAMBIO 1: APLICAR LA CONFIGURACIÓN DE CORS! ---
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            
            // (Deshabilita CSRF, necesario para que las peticiones de tu API funcionen)
            .csrf(csrf -> csrf.disable()) 
            
            .authorizeHttpRequests(authorize -> authorize
                
                // --- ¡CAMBIO 2: AÑADIR PERMISO A /img/** Y CSS! ---
                // Damos permiso público a la API, las imágenes, el css y el login.
                .requestMatchers(
                    "/api/v1/productos/**", 
                    "/img/**",  // <-- ¡PERMISO PARA IMÁGENES!
                    "/css/**",  // <-- (Permiso para tu CSS que faltaba)
                    "/login"
                ).permitAll()
                
                // Protegemos el panel de admin
                .requestMatchers("/admin/**").hasRole("ADMIN")
                
                // CUALQUIER otra ruta debe estar autenticada
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage("/login") 
                .successHandler(successHandler) 
                .permitAll()
            )
            .logout(logout -> logout
                .logoutSuccessUrl("/") 
                .permitAll()
            );

        return http.build();
    }
}