package com.example.segundoAvance.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Table(name = "pedido")
public class Pedido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ... (Todos tus otros campos: nombre, apellidos, email, etc. están bien)
    private String nombre;
    private String apellidos;
    private String email;
    private String telefono;
    private String direccion;
    private String referencia;
    private String departamento;
    private String provincia;
    private String distrito;
    private String tipoComprobante;
    private String dni;
    private String ruc;
    private String razonSocial;
    private String metodoEnvio;
    private String metodoPago;
    private BigDecimal costoEnvio;
    private BigDecimal subtotal;
    private BigDecimal total;
    private String estado;
    private LocalDateTime fecha;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    @JsonIgnore // Evita bucles al serializar
    private Usuario usuario;

    @OneToMany(
            mappedBy = "pedido",
            cascade = CascadeType.ALL, // Guarda/Actualiza/Borra detalles junto con el pedido
            orphanRemoval = true,
            fetch = FetchType.EAGER // Cambiado a EAGER para que los detalles se carguen con el pedido
    )
    private List<DetallePedido> detalles = new ArrayList<>();


    // --- ¡AQUÍ ESTÁ EL ARREGLO! ---
    // Este método ayudante sincroniza la relación
    // desde ambos lados (Pedido y DetallePedido).
    public void addDetalle(DetallePedido detalle) {
        detalles.add(detalle);
        detalle.setPedido(this);
    }
}