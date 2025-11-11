package com.example.segundoAvance;

import com.example.segundoAvance.model.Usuario;
import com.example.segundoAvance.repository.UsuarioRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootApplication
public class SegundoAvanceApplication {

	public static void main(String[] args) {
		SpringApplication.run(SegundoAvanceApplication.class, args);
	}

	// ***** CÓDIGO AÑADIDO AQUÍ *****
	// Este bloque crea el usuario administrador por defecto al iniciar la aplicación.
	@Bean
	CommandLineRunner init(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
		return args -> {
			// Comprueba si el admin ya existe en la base de datos
			if (usuarioRepository.findByEmail("admin@tienda.com").isEmpty()) {
				
				Usuario admin = new Usuario();
				admin.setNombreCompleto("Administrador");
				admin.setEmail("admin@tienda.com");
				admin.setPassword(passwordEncoder.encode("admin123")); // Contraseña codificada
				admin.setRol("ROLE_ADMIN"); // Rol de administrador
				
				usuarioRepository.save(admin);
				
				System.out.println(">>> Usuario administrador por defecto creado con éxito <<<");
			}
		};
	}
}