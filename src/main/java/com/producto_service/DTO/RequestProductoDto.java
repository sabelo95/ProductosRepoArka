package com.producto_service.DTO;

import com.producto_service.Model.Categoria;
import com.producto_service.Model.Marca;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RequestProductoDto {

    @NotBlank(message = "El nombre no puede estar vacío")
    @Size(max = 100, message = "El nombre no puede tener más de 100 caracteres")
    private String nombre;

    @NotBlank(message = "La descripción no puede estar vacía")
    @Size(max = 500, message = "La descripción no puede tener más de 500 caracteres")
    private String descripcion;
    @Min(value = 0, message = "La cantidad no puede ser negativa")
    private Integer cantidad;
    @NotNull(message = "La categoría no puede ser nula")
    @Valid
    private Categoria categoria;
    @NotEmpty(message = "La marca no puede estar vacía")
    @Valid
    private Marca marca;
    @NotBlank(message = "El precio no puede estar vacío")
    private Double precio;
}
