package com.example.segundoAvance.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Entity
@Table(name = "detalle_pedido")
public class DetallePedido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * ¡LA RELACIÓN PRINCIPAL!
     * Muchos detalles pertenecen a Un Pedido.
     * Esta es la columna 'pedido_id' que enlaza todo.
     * Usamos @JsonIgnore para evitar bucles infinitos al mostrar los pedidos.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pedido_id", nullable = false)
    @JsonIgnore
    private Pedido pedido;

    /**
     * La relación con el producto que se compró.
     * Muchos detalles pueden apuntar a Un Producto.
     *
     * --- ¡AQUÍ ESTÁ EL ARREGLO! ---
     * Cambiamos LAZY por EAGER para que el producto se cargue 
     * junto con el detalle, evitando el LazyInitializationException.
     */
    @ManyToOne(fetch = FetchType.EAGER) // <-- LÍNEA CORREGIDA
    @JoinColumn(name = "producto_id", nullable = false)
    private Producto producto;

    @Column(nullable = false)
    private int cantidad;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal precioUnitario; // Precio al momento de la compra
}