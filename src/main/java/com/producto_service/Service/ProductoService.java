package com.producto_service.Service;
import com.producto_service.DTO.RequestProductoDto;
import com.producto_service.DTO.ProductoResponseDto;
import com.producto_service.Mapper.ProductoMapper;
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

    public List<String> obtenerProductosPorMarca(String marcaNombre) {
        if (marcaNombre == null || marcaNombre.isEmpty()) {
            throw new IllegalArgumentException("El nombre de la marca no puede ser nulo o vacío.");
        }
        if (marcaService.obtenerMarcaPorNombre(marcaNombre) == null) {
            throw new IllegalArgumentException("La marca con nombre " + marcaNombre + " no existe.");
        }
        List<Producto> productos= productoRepository.findByMarcaNombre(marcaNombre);
        List<String> nombresProductos = new ArrayList<>();
        for (Producto producto : productos) {
            nombresProductos.add(producto.getNombre());
        }
        return nombresProductos;
    }

    public List<Producto> obtenerProductosPorIds(List<Long> ids) {
        return productoRepository.findAllById(ids);
    }

    @Transactional
    public ProductoResponseDto crearProducto(RequestProductoDto productoDto) {


        Producto producto = new Producto();
        producto.setNombre(productoDto.getNombre());
        producto.setDescripcion(productoDto.getDescripcion());
        producto.setCantidad(productoDto.getCantidad());
        if (categoriaService.validarCategoria(productoDto.getCategoria()) != null) {
            producto.setCategoria(productoDto.getCategoria());
        } else {
            throw new IllegalArgumentException("La categoría con ID " + productoDto.getCategoria().getId() + " no existe.");
        }

        if (marcaService.validarMarca(productoDto.getMarca().getNombre()) != null) {
            producto.setMarca(productoDto.getMarca());
        } else {
            throw new IllegalArgumentException("La marca con ID " + productoDto.getMarca().getId() + " no existe.");
        }
        producto.setPrecio(productoDto.getPrecio());


;
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

    public Producto actualizarProducto(String nombre, RequestProductoDto productoDto) {
        Producto productoExistente = obtenerProductoPorNombre(nombre);
        if (productoExistente == null) {
            throw new IllegalArgumentException("El producto con nombre " + nombre + " no existe.");
        }


        if (productoDto.getNombre() != null) {
            productoExistente.setNombre(productoDto.getNombre());
        }
        if (productoDto.getDescripcion() != null) {
            productoExistente.setDescripcion(productoDto.getDescripcion());
        }
        if (productoDto.getCategoria() != null) {
            productoExistente.setCategoria(productoDto.getCategoria());
        }

        if (productoDto.getDetalleProductoMarcas() != null && !productoDto.getDetalleProductoMarcas().isEmpty()) {

            detalleProductoMarcaRepository.deleteAll(productoExistente.getDetalleProductoMarca());
            productoExistente.getDetalleProductoMarca().clear();


            int cantidadStock = productoDto.getDetalleProductoMarcas()
                    .stream()
                    .mapToInt(DetalleProductoMarca::getCantidad)
                    .sum();
            productoExistente.setCantidad(cantidadStock);


            for (DetalleProductoMarca detalleDto : productoDto.getDetalleProductoMarcas()) {
                Marca marca = marcaService.obtenerMarcaPorNombre(detalleDto.getMarca().getNombre());
                if (marca == null) {
                    throw new IllegalArgumentException("La marca con nombre " + detalleDto.getMarca().getNombre() + " no existe.");
                }
                DetalleProductoMarca detalle = new DetalleProductoMarca();
                detalle.setMarca(marca);
                detalle.setCantidad(detalleDto.getCantidad());
                detalle.setPrecio(detalleDto.getPrecio());
                detalle.setProducto(productoExistente);
                productoExistente.getDetalleProductoMarca().add(detalle);
            }
        }

        return productoRepository.save(productoExistente);
    }






}
