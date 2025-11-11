package com.example.segundoAvance.repository;

import com.example.segundoAvance.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    // Spring Data JPA usará este método para buscar un usuario por su email
    // al momento de iniciar sesión.
    Optional<Usuario> findByEmail(String email);
}