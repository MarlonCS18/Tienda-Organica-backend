package com.example.segundoAvance.dto;

import lombok.Data;

@Data
public class ClienteInfoDTO {
    private String email;
    private String nombre;
    private String apellidos;
    private String tipoComprobante;
    private String dni;
    private String ruc;
    private String razonSocial;
    private String telefono;
    private String direccion;
    private String referencia;
    private String departamento;
    private String provincia;
    private String distrito;
}