package com.example.segundoAvance.config;

import com.example.segundoAvance.config.handler.CustomAuthenticationSuccessHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
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

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        // Tu configuración CORS está perfecta
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
                
                // 1. Permite todas las solicitudes OPTIONS (para CORS)
                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll() 
                
                // 2. Permite todas las rutas públicas (GET y generales)
                .requestMatchers(
                    "/", "/login", "/registro",         // Páginas Thymeleaf
                    "/css/**", "/img/**",                // Recursos estáticos
                    "/api/v1/productos/**",             // API pública de productos
                    "/api/v1/auth/me"                   // API pública de sesión
                ).permitAll()
                
                // 3. Permite todas las rutas POST públicas (separadas)
                .requestMatchers(HttpMethod.POST,
                    "/login",                   // El form login de Spring
                    "/api/v1/auth/register",    // Registro de tu frontend
                    "/api/v1/auth/logout"       // Logout de tu frontend
                ).permitAll()

                // 4. Protege el admin
                .requestMatchers("/admin/**").hasRole("ADMIN")
                
                // 5. Protege rutas de usuario específicas
                .requestMatchers("/api/v1/pedidos/**").hasAnyRole("USER", "ADMIN")

                // 6. Protege todo lo demás
                .anyRequest().authenticated()
            )
            
            .formLogin(form -> form
                .loginPage("/login") 
                .successHandler(successHandler) 
                .failureHandler((request, response, exception) -> {
                    response.sendRedirect("/login?error=true");
                })
            )
            
            // --- ¡AQUÍ ESTÁ EL ARREGLO! ---
            .logout(logout -> logout
                .logoutUrl("/api/v1/auth/logout") // Escucha en la URL del frontend
                .logoutSuccessUrl("/")
                .invalidateHttpSession(true)
                // 1. Ahora le decimos que BORRE AMBAS cookies al cerrar sesión
                .deleteCookies("JSESSIONID", "remember-me") 
            )
            
            // 2. Le decimos a Spring que NUNCA MÁS vuelva a crear
            //    la cookie "remember-me"
            .rememberMe(rm -> rm.disable());
            // --- FIN DEL ARREGLO ---

        return http.build();
    }
}