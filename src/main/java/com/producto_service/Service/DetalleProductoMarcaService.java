package com.producto_service.Service;

import com.producto_service.Model.Marca;
import com.producto_service.Model.Producto;
import com.producto_service.Repository.DetalleProductoMarcaRepository;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.stereotype.Service;

@Service
@Data
@AllArgsConstructor
public class DetalleProductoMarcaService {

    private final HistorialService historialService;
    private final DetalleProductoMarcaRepository detalleProductoMarcaRepository;
    private final MarcaService marcaService;
    private final ProductoService productoService;

    public void ActualizarStock(String nombreProducto, String nombreMarca, int nuevaCantidad) {

    if (nuevaCantidad < 0) {
            throw new IllegalArgumentException("La cantidad no puede ser negativa");
        }
        if (nombreProducto == null || nombreProducto.isEmpty()) {
            throw new IllegalArgumentException("El nombre del producto no puede ser nulo o vacío");
        }
        if (nombreMarca == null || nombreMarca.isEmpty()) {
            throw new IllegalArgumentException("El nombre de la marca no puede ser nulo o vacío ");
        }
        if (!marcaService.validarMarca(nombreMarca)) {
            throw new IllegalArgumentException("La marca con nombre " + nombreMarca + " no existe.");
        }

        Producto producto = productoService.obtenerTodosLosProductos()
                .stream()
                .filter(p -> p.getNombre().equals(nombreProducto))
                .findFirst()
                .orElse(null);

        if (producto == null) {
            throw new IllegalArgumentException("El producto con nombre " + nombreProducto + " no existe.");
        }

        Marca marca = marcaService.obtenerTodasLasMarcas()
                .stream()
                .filter(m -> m.getNombre().equals(nombreMarca))
                .findFirst()
                .orElse(null);

        if (marca == null) {
            throw new IllegalArgumentException("La marca con nombre " + nombreMarca + " no existe.");
        }


        var detalleProductoMarca = detalleProductoMarcaRepository.findByProducto_IdAndMarca_Id(producto.getId(), marca.getId());
        if (detalleProductoMarca.isPresent()) {
            var detalle = detalleProductoMarca.get();
            detalle.setCantidad(nuevaCantidad);
            detalleProductoMarcaRepository.save(detalle);
            historialService.agregarHistorial(detalle);
        } else {
            throw new IllegalArgumentException("No se encontró el detalle del producto con la marca especificada.");
        }

    }
}
