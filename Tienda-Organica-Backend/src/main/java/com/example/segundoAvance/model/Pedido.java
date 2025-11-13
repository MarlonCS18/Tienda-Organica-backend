package com.example.segundoAvance.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@Entity
public class Pedido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @CreationTimestamp
    private LocalDateTime fecha;

    @Column(columnDefinition = "TEXT")
    private String detalles; // JSON de los items

    private Double subtotal;
    private Double costoEnvio;
    private Double total;

    // --- DATOS DEL CLIENTE ---
    private String email;
    private String nombre;
    private String apellidos;
    private String tipoComprobante; // "boleta" o "factura"
    private String dni;
    private String ruc;
    private String razonSocial;
    private String telefono;
    
    // --- DATOS DE ENVÍO ---
    private String direccion;
    private String referencia;
    private String distrito;
    private String provincia;
    private String departamento;

    // --- DATOS DE PAGO ---
    private String metodoEnvio;
    private String metodoPago;
    private String estado = "PENDIENTE";

    // --- ¡AQUÍ ESTÁ LA CORRECCIÓN! ---
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
        name = "usuario_id", // La columna en la tabla 'pedido'
        // Le decimos explícitamente que la clave foránea apunta
        // a la tabla 'usuarios' (plural)
        foreignKey = @ForeignKey(name = "FK_pedido_usuarios") 
    )
    private Usuario usuario;
}