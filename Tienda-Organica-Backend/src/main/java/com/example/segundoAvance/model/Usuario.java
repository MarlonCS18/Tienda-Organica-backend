package com.example.segundoAvance.model;

import jakarta.persistence.*;
import lombok.Data;
import java.util.List;

@Data
@Entity
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombreCompleto;
    private String email;
    private String password;
    private String rol;
    private String telefono;
    private String direccion;

    // --- RELACIÓN "UNO A MUCHOS" CON PEDIDO ---
    @OneToMany(mappedBy = "usuario")
    // ***** CAMBIO APLICADO AQUÍ *****
    @OrderBy("id DESC") // Ordena la lista de pedidos del más reciente al más antiguo
    private List<Pedido> pedidos;
}