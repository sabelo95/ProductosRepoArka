package com.producto_service.Mapper;

import com.producto_service.DTO.DetalleProductoMarcaResponseDto;
import com.producto_service.DTO.ProductoResponseDto;
import com.producto_service.Model.Producto;
import org.springframework.stereotype.Component;

import java.util.List;
@Component
public class ProductoMapper {

    public ProductoResponseDto mapToResponseDto(Producto producto) {
        List<DetalleProductoMarcaResponseDto> detallesDto = producto.getDetalleProductoMarca().stream()
                .map(detalle -> new DetalleProductoMarcaResponseDto(
                        detalle.getId(),
                        detalle.getMarca().getNombre(),
                        detalle.getCantidad(),
                        detalle.getPrecio()
                ))
                .toList();

        return new ProductoResponseDto(
                producto.getId(),
                producto.getNombre(),
                producto.getDescripcion(),
                producto.getCantidad(),
                producto.getCategoria(),
                detallesDto
        );
    }
}
