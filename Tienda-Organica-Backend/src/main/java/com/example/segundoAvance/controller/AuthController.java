package com.example.segundoAvance.controller;

// 1. IMPORTACIONES DE LIMPIEZA
// Se eliminaron las importaciones no usadas (Authentication, BCryptPasswordEncoder, etc.)
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
// Se eliminó @PostMapping, @RequestBody, @Autowired, etc.

@Controller
public class AuthController {

    // 2. MÉTODO NUEVO PARA LA RAÍZ
    /**
     * Redirige la URL raíz (http://localhost:8080/) 
     * a la página de inicio de sesión.
     */
    @GetMapping("/")
    public String redirectToLogin() {
        return "redirect:/login";
    }

    // 3. MÉTODO GET PARA MOSTRAR EL LOGIN
    /**
     * Muestra la plantilla de Thymeleaf 'login.html'
     * cuando alguien visita http://localhost:8080/login
     */
    @GetMapping("/login")
    public String showLoginForm() {
        return "login"; // Devuelve el nombre del archivo 'login.html'
    }

    // 4. MÉTODO GET PARA MOSTRAR EL REGISTRO
    /**
     * Muestra la plantilla 'registro.html'
     * (Basado en tu AuthController original)
     */
    @GetMapping("/registro")
    public String registroForm() {
        return "registro"; // Devuelve el nombre del archivo 'registro.html'
    }


    // 5. MÉTODOS POST ELIMINADOS
    // Se eliminó el método @PostMapping("/login")
    // porque Spring Security (en tu SecurityConfig) 
    // lo maneja automáticamente.
    
    // Se eliminó el método @PostMapping("/registro")
    // porque tu ApiAuthController se encarga de eso.
}