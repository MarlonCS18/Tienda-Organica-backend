package com.example.segundoAvance.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AuthController {

    /**
     * Este controlador ahora sí responderá en la ruta "/login"
     * y le dirá a Thymeleaf que muestre la plantilla "login.html".
     * Esto rompe el bucle de redirección.
     */
    @GetMapping("/login")
    public String login() {
        // Devuelve el nombre de la plantilla: /resources/templates/login.html
        return "login"; 
    }

    /**
     * ADICIONAL: Ya que tu SecurityConfig redirige a "/"
     * cuando cierras sesión, vamos a hacer que "/" también
     * te redirija al login.
     */
    @GetMapping("/")
    public String home() {
        return "redirect:/login";
    }
}