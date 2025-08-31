package com.producto_service.Service;
import com.producto_service.DTO.DetalleProductoMarcaResponseDto;
import com.producto_service.DTO.ProductoDto;
import com.producto_service.DTO.ProductoResponseDto;
import com.producto_service.Mapper.ProductoMapper;
import com.producto_service.Model.DetalleProductoMarca;
import com.producto_service.Model.Marca;
import com.producto_service.Model.Producto;

import com.producto_service.Repository.ProductoRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@Data
@AllArgsConstructor
public class ProductoService {

    private final ProductoRepository productoRepository;
    private final MarcaService marcaService;
    private final CategoriaService categoriaService;
    private final HistorialService historialService;
    private final ProductoMapper productoMapper;



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

//    public List<Producto> obtenerProductosPorMarca(String marcaNombre) {
//        if (marcaNombre == null || marcaNombre.isEmpty()) {
//            throw new IllegalArgumentException("El nombre de la marca no puede ser nulo o vacío.");
//        }
//        if (marcaService.obtenerMarcaPorNombre(marcaNombre) == null) {
//            throw new IllegalArgumentException("La marca con nombre " + marcaNombre + " no existe.");
//        }
//        return productoRepository.findByMarcaNombre(marcaNombre);
//    }

    public List<Producto> obtenerProductosPorIds(List<Long> ids) {
        return productoRepository.findAllById(ids);
    }

    @Transactional
    public ProductoResponseDto crearProducto(ProductoDto productoDto) {

        int cantidadStock = productoDto.getDetalleProductoMarcas()
                .stream()
                .mapToInt(DetalleProductoMarca::getCantidad)
                .sum();


        Producto producto = new Producto();
        producto.setNombre(productoDto.getNombre());
        producto.setDescripcion(productoDto.getDescripcion());
        producto.setCantidad(cantidadStock);
        producto.setCategoria(productoDto.getCategoria());


        List<DetalleProductoMarca> detalles = new ArrayList<>();
        for (DetalleProductoMarca detalleDto : productoDto.getDetalleProductoMarcas()) {
            String marcaNombre = detalleDto.getMarca().getNombre();
            Marca marca = marcaService.obtenerMarcaPorNombre(marcaNombre);
            if (marca == null) {
                throw new IllegalArgumentException("La marca con nombre " + marcaNombre + " no existe.");
            }
            DetalleProductoMarca detalle = new DetalleProductoMarca();
            detalle.setMarca(marca);
            detalle.setCantidad(detalleDto.getCantidad());
            detalle.setPrecio(detalleDto.getPrecio());
            detalle.setProducto(producto);
            detalles.add(detalle);
        }

        producto.setDetalleProductoMarca(detalles);


        Producto productoGuardado = productoRepository.save(producto);


        return productoMapper.mapToResponseDto(productoGuardado);
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
