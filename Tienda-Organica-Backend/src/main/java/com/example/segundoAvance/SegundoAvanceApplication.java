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
            
            // (Esta línea ya estaba bien en tu archivo)
            admin.setNombre("Administrador");
            
            // --- ¡AQUÍ ESTÁ LA CORRECCIÓN! ---
            // Cambiamos 'setRol' por 'setRoles' (plural)
            admin.setRoles("ROLE_ADMIN"); // <-- LÍNEA CORREGIDA
            
            usuarioRepository.save(admin);
            System.out.println(">>> Usuario Administrador (admin@tienda.com) creado por defecto <<<");
        }
    }
}