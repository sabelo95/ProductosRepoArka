package com.producto_service.DTO;

import com.producto_service.Model.Categoria;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductoResponseDto {
    private Long id;
    private String nombre;
    private String descripcion;
    private Integer cantidad;
    private Categoria categoria;
    private List<DetalleProductoMarcaResponseDto> detalleProductoMarcas;
}
