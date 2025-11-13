package com.example.segundoAvance;

import com.example.segundoAvance.model.Usuario;
import com.example.segundoAvance.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootApplication
public class SegundoAvanceApplication implements CommandLineRunner {

    public static void main(String[] args) {
        SpringApplication.run(SegundoAvanceApplication.class, args);
    }

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        // Esta lógica crea un usuario ADMIN si no existe ninguno
        if (usuarioRepository.count() == 0) {
            Usuario admin = new Usuario();
            
            // --- TUS DATOS ORIGINALES ---
            admin.setEmail("admin@tienda.com");
            admin.setPassword(passwordEncoder.encode("admin123"));
            
            // --- ¡LA CORRECCIÓN! ---
            // Cambiamos 'setNombreCompleto' por 'setNombre'
            admin.setNombre("Administrador"); // <-- LÍNEA CORREGIDA
            
            admin.setRol("ROLE_ADMIN");
            usuarioRepository.save(admin);
            System.out.println(">>> Usuario Administrador (admin@tienda.com) creado por defecto <<<");
        }
    }
}