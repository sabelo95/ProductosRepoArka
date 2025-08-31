package com.producto_service.Service;
import com.producto_service.DTO.ProductoDto;
import com.producto_service.Model.Producto;

import com.producto_service.Repository.ProductoRepository;
import lombok.Data;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@Data
public class ProductoService {

    private final ProductoRepository productoRepository;
    private final MarcaService marcaService;
    private final CategoriaService categoriaService;
    private final HistorialService historialService;


    public ProductoService(ProductoRepository productoRepository, MarcaService marcaService,
                           CategoriaService categoriaService, HistorialService historialService) {
        this.historialService = historialService;
        this.categoriaService = categoriaService;
        this.marcaService = marcaService;
        this.productoRepository = productoRepository;


    }

    public List<Producto> obtenerTodosLosProductos() {
        return productoRepository.findAll();

    }

    public List<Producto> obtenerProductosPorCategoria(String categoriaNombre){
    if (categoriaNombre == null || categoriaNombre.isEmpty()) {
            throw new IllegalArgumentException("El nombre de la categoría no puede ser nulo o vacío.");
        }
    if (categoriaService.obtenerCategoriaPorNombre(categoriaNombre) == null) {
            throw new IllegalArgumentException("La categoría con nombre " + categoriaNombre + " no existe.");
        }

    return productoRepository.findByCategoriaNombre(categoriaNombre);
    }

    public List<Producto> obtenerProductosPorMarca(String marcaNombre) {
        if (marcaNombre == null || marcaNombre.isEmpty()) {
            throw new IllegalArgumentException("El nombre de la marca no puede ser nulo o vacío.");
        }
        if (marcaService.obtenerMarcaPorNombre(marcaNombre) == null) {
            throw new IllegalArgumentException("La marca con nombre " + marcaNombre + " no existe.");
        }
        return productoRepository.findByMarcaNombre(marcaNombre);
    }

    public List<Producto> obtenerProductosPorIds(List<Long> ids) {
        return productoRepository.findAllById(ids);
    }

    public Producto crearProducto(ProductoDto productoDto) {
        marcaService.validarMarca(productoDto.getMarcas());
        categoriaService.validarCategoria(productoDto.getCategoria());
        Producto producto = new Producto();
        producto.setNombre(productoDto.getNombre());
        producto.setDescripcion(productoDto.getDescripcion());
        producto.setPrecio(productoDto.getPrecio());
        producto.setCantidad(productoDto.getCantidad());
        producto.setCategoria(productoDto.getCategoria());
        producto.setMarcas(productoDto.getMarcas());
        return productoRepository.save(producto);
    }

    public Producto obtenerProductoPorNombre(String nombre) {
        return productoRepository.findByNombre(nombre)
                .orElseThrow(() -> new IllegalArgumentException("Producto no encontrado con nombre: " + nombre));
    }
    public void eliminarProducto(String nombre) {
        Producto producto = productoRepository.findByNombre(nombre)
                .orElseThrow(() -> new IllegalArgumentException("Producto no encontrado con nombre: " + nombre));
        productoRepository.delete(producto);
    }

    public String actualizarStock(String nombre, Integer cantidad){
        Producto producto = productoRepository.findByNombre(nombre)
                .orElseThrow(() -> new IllegalArgumentException("Producto no encontrado con nombre: " + nombre));
        if (cantidad < 0) {
            throw new IllegalArgumentException("La cantidad no puede ser negativa.");
        }
        producto.setCantidad(cantidad);
        productoRepository.save(producto);
        historialService.agregarHistorial(producto);
        return "Stock actualizado correctamente.";
    }

}
