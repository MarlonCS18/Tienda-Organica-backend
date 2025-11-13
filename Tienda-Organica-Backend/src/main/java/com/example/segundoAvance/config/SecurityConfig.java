package com.example.segundoAvance.config;

import com.example.segundoAvance.config.handler.CustomAuthenticationSuccessHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod; 
import org.springframework.http.HttpStatus; 
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.logout.HttpStatusReturningLogoutSuccessHandler; 
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import java.util.Arrays;

@Configuration
public class SecurityConfig {

    @Autowired
    private CustomAuthenticationSuccessHandler successHandler; // ¡Volvemos a usar tu handler!

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList("http://localhost:3000")); 
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("Authorization", "Cache-Control", "Content-Type", "X-Requested-With"));
        configuration.setAllowCredentials(true); 
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration); 
        return source;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .csrf(csrf -> csrf.disable()) 
            
            .authorizeHttpRequests(authorize -> authorize
                
                // --- RUTAS PÚBLICAS (Sin cambios) ---
                .requestMatchers(
                    "/css/**", 
                    "/img/**", 
                    "/api/v1/productos/**"
                ).permitAll()
                .requestMatchers(
                    "/login", 
                    "/api/v1/auth/register",
                    "/api/v1/auth/me"
                ).permitAll()
                .requestMatchers(HttpMethod.POST, "/login").permitAll() 

                // --- RUTAS DE ADMIN (Sin cambios) ---
                .requestMatchers("/admin/**").hasRole("ADMIN")
                
                // --- RUTAS PROTEGIDAS (Sin cambios) ---
                .requestMatchers(
                    "/api/v1/pedidos/crear"
                ).hasAnyRole("USER", "ADMIN")

                .anyRequest().authenticated()
            )
            
            // --- ¡CONFIGURACIÓN DE LOGIN CORREGIDA! ---
            .formLogin(form -> form
                .loginPage("/login")
                // 1. Usamos tu CustomSuccessHandler. Este SÍ SABE redirigir
                // al admin a /admin/dashboard y al usuario a /
                .successHandler(successHandler) 
                // 2. Quitamos el .failureHandler() personalizado.
                // Ahora Spring usará el por defecto, que redirige a /login?error
                // Esto arregla tu error 401 en la página de admin.
                .permitAll() 
            )
            
            // --- LOGOUT (Sin cambios) ---
            .logout(logout -> logout
                .logoutUrl("/api/v1/auth/logout") 
                .logoutSuccessHandler(new HttpStatusReturningLogoutSuccessHandler(HttpStatus.OK)) 
                .deleteCookies("JSESSIONID")
                .permitAll()
            );

        return http.build();
    }
}