package com.example.segundoAvance.controller;

import com.example.segundoAvance.dto.RegistroDTO;
import com.example.segundoAvance.dto.UsuarioDTO;
import com.example.segundoAvance.model.Usuario;
import com.example.segundoAvance.repository.UsuarioRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.security.Principal;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/auth")
// --- ¡AQUÍ ESTÁ EL ARREGLO! ---
// Se eliminó la anotación @CrossOrigin. Dejaremos que SecurityConfig lo maneje.
// @CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true") 
public class ApiAuthController {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * Endpoint para que un nuevo usuario se registre.
     */
    @PostMapping("/register")
    public ResponseEntity<?> registrarUsuario(@RequestBody RegistroDTO registroDTO) {
        if (usuarioRepository.findByEmail(registroDTO.getEmail()).isPresent()) {
            return ResponseEntity
                    .status(HttpStatus.CONFLICT)
                    .body(Map.of("error", "El correo electrónico ya está registrado."));
        }

        Usuario nuevoUsuario = new Usuario();
        nuevoUsuario.setNombre(registroDTO.getNombre());
        nuevoUsuario.setEmail(registroDTO.getEmail());
        nuevoUsuario.setPassword(passwordEncoder.encode(registroDTO.getPassword()));
        
        // (Esto ya debería estar corregido de la vez anterior)
        nuevoUsuario.setRoles("ROLE_USER"); // Rol por defecto para clientes

        usuarioRepository.save(nuevoUsuario);

        return ResponseEntity.created(URI.create("/login")).build();
    }

    /**
     * Endpoint para que React verifique si hay una sesión activa.
     */
    @GetMapping("/me")
    public ResponseEntity<UsuarioDTO> obtenerEstadoAutenticacion(Principal principal) {
        if (principal == null) {
            // No hay nadie logueado
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        // Si hay alguien logueado, buscar sus datos
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        
        Usuario usuario = usuarioRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado en /me"));

        // Devolver un DTO seguro (sin la contraseña)
        UsuarioDTO usuarioDTO = new UsuarioDTO(
                usuario.getId(),
                usuario.getNombre(),
                usuario.getEmail(),
                // (Esto ya debería estar corregido de la vez anterior)
                usuario.getRoles()
        );
        return ResponseEntity.ok(usuarioDTO);
    }
}