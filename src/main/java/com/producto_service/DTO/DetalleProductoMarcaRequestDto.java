package com.producto_service.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DetalleProductoMarcaRequestDto {
    private String productoNombre;
    private String marcaNombre;
    private int cantidad;

}
