package com.example.segundoAvance.dto;

import lombok.Data;
import java.util.List;

@Data
public class PedidoRequestDTO {
    private ClienteInfoDTO clienteInfo;
    private List<ItemDTO> items;
    private String metodoEnvio;
    private String metodoPago;
    private double costoEnvio;
    private double subtotal;
    private double total;
}