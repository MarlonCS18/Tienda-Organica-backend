package com.example.segundoAvance.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Data
@Entity
@Table(name = "usuario")
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nombre;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    private String roles; // El campo se llama 'roles' (plural)

    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore
    private List<Pedido> pedidos;


    // --- ¡AQUÍ ESTÁ EL ARREGLO! ---
    // Añadimos el "setter" manualmente para que Java lo encuentre
    // y el error en AdminController desaparezca.
    public void setRoles(String roles) {
        this.roles = roles;
    }
}