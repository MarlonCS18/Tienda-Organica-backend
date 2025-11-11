package com.example.segundoAvance.repository;

import com.example.segundoAvance.model.Producto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List; // Asegúrate de importar List

@Repository
public interface ProductoRepository extends JpaRepository<Producto, Long> {

    // ***** MÉTODO NUEVO AÑADIDO *****
    // Spring Data JPA entenderá automáticamente que este método
    // debe buscar todos los productos donde el campo 'categoria' coincida.
    List<Producto> findByCategoria(String categoria);
    List<Producto> findTop4ByOrderByIdDesc();
}