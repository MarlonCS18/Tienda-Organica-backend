package com.example.segundoAvance.dto;

import lombok.Data;
import java.math.BigDecimal; // <-- 1. ASEGÚRATE DE IMPORTAR ESTO
import java.util.List;

@Data
public class PedidoRequestDTO {

    private ClienteInfoDTO clienteInfo;
    private List<ItemDTO> items;
    private String metodoEnvio;
    private String metodoPago;
    
    // --- AQUÍ ESTÁ EL ARREGLO ---
    // Cambiamos 'double' por 'BigDecimal' para que coincida
    // con la entidad Pedido y el ItemDTO.
    
    private BigDecimal costoEnvio; // <-- 2. CAMBIADO
    private BigDecimal subtotal;   // <-- 3. CAMBIADO
    private BigDecimal total;      // <-- 4. CAMBIADO
}