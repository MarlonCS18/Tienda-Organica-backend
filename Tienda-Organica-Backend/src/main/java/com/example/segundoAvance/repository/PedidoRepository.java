package com.example.segundoAvance.repository;

import com.example.segundoAvance.model.Pedido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PedidoRepository extends JpaRepository<Pedido, Long> {
    // Spring Data JPA se encargará de todo.
}