package com.example.segundoAvance.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
public class Pedido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDateTime fechaCreacion;

    private double total;

    // --- RELACIÓN "MUCHOS A UNO" CON USUARIO ---
    // Muchos pedidos pueden pertenecer a un solo usuario.
    @ManyToOne
    @JoinColumn(name = "usuario_id") // Esta será la clave foránea en la tabla pedido.
    private Usuario usuario;

    // --- RELACIÓN "MUCHOS A MUCHOS" CON PRODUCTO ---
    // Un pedido puede tener muchos productos.
    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinTable(
        name = "pedido_productos", // Nombre de la tabla intermedia
        joinColumns = @JoinColumn(name = "pedido_id"),
        inverseJoinColumns = @JoinColumn(name = "producto_id")
    )
    private List<Producto> productos = new ArrayList<>();
    
    // Podríamos añadir más detalles como la cantidad de cada producto
    // pero para empezar, esta es la relación básica.
}