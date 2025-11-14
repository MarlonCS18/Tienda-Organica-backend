package com.example.segundoAvance.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class ItemDTO {
    private Long id;
    private int quantity;
    private BigDecimal precio;
}