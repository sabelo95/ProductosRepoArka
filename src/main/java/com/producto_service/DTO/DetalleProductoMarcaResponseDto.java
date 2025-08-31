package com.producto_service.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DetalleProductoMarcaResponseDto {
    private Long id;
    private String marcaNombre;
    private int cantidad;
    private double precio;
}