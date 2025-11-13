package com.example.segundoAvance.repository;

import com.example.segundoAvance.model.Pedido;
import com.example.segundoAvance.model.Usuario;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface PedidoRepository extends JpaRepository<Pedido, Long> {
    // Spring Data JPA se encargará de todo.
    List<Pedido> findByUsuarioOrderByIdDesc(Usuario usuario);
}