package com.producto_service.Controller;

import com.producto_service.DTO.RequestProductoDto;
import com.producto_service.DTO.ProductoResponseDto;
import com.producto_service.Model.Producto;
import com.producto_service.Service.ProductoService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/productos")
public class ProductoController {


    private ProductoService productoService;

    ProductoController(ProductoService productoService) {
        this.productoService = productoService;
    }

    @GetMapping("/todos")
    public List<Producto> obtenerTodos() {
        return productoService.obtenerTodosLosProductos();
    }

    @GetMapping("/categoria/{nombre}")
    public List<Producto> obtenerPorCategoria(@PathVariable String nombre) {
        return productoService.obtenerProductosPorCategoria(nombre);
        }


        @GetMapping("/marca/{nombre}")
        public List<String> obtenerPorMarca(@PathVariable String nombre) {
            return productoService.obtenerProductosPorMarca(nombre);
        }

        @GetMapping("/lista-ids")
        public List<Producto> obtenerPorIds(@RequestParam List<Long> ids) {
        return productoService.obtenerProductosPorIds(ids);
     }


    @PostMapping()
    @PreAuthorize("hasRole('ADMINISTRADOR')")
        public ResponseEntity<?> crearProducto(@Valid @RequestBody RequestProductoDto producto) {
            try {
                ProductoResponseDto nuevo = productoService.crearProducto(producto);
                return ResponseEntity.status(HttpStatus.CREATED).body(nuevo);
            } catch (IllegalArgumentException e) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
            }
        }

        @GetMapping("/{nombre}")
        public ResponseEntity<Producto> obtenerPorId(@PathVariable String id) {
            try {
                Producto producto = productoService.obtenerProductoPorNombre(id);
                return ResponseEntity.ok(producto);
            } catch (IllegalArgumentException e) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
            }
        }

        @DeleteMapping("/{nombre}")
        @PreAuthorize("hasRole('ADMINISTRADOR')")
        public ResponseEntity<?> eliminarProducto(@PathVariable String nombre) {
            try {
                productoService.eliminarProducto(nombre);
                return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
            } catch (IllegalArgumentException e) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
            }
        }

    @PutMapping("/actualizar/{nombre}")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<?> actualizarProducto(@PathVariable String nombre, @RequestBody RequestProductoDto producto) {
        try {
            Producto productoExistente = productoService.obtenerProductoPorNombre(nombre);
            if (productoExistente == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("El producto con nombre " + nombre + " no existe.");
            }
            Producto actualizado = productoService.actualizarProducto(nombre, producto);
            return ResponseEntity.ok(actualizado);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

      @PostMapping("/reducir-stock")
      public void reduccionStock(@RequestBody Map<Long, Integer> productos) {
          productoService.reduccionStock(productos);

          }


    @PostMapping("/reposicion-stock")
    public void reposicionStock(@RequestBody Map<Long, Integer> productos) {
        productoService.reposicionStock(productos);

    }







}
