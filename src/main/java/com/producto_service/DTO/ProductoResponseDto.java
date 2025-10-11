package com.producto_service.DTO;

import com.producto_service.Model.Categoria;
import com.producto_service.Model.Marca;
import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ProductoResponseDto {
    private String nombre;
    private String descripcion;
    private Integer cantidad;
    private Double precio;
    private Categoria categoria;
    private Marca marca;
}
