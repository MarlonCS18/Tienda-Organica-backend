package com.example.segundoAvance.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CarritoItem {

    private Producto producto;
    private int cantidad;

    // Método para calcular el subtotal de este ítem
    public double getSubtotal() {
        return producto.getPrecio() * cantidad;
    }
}