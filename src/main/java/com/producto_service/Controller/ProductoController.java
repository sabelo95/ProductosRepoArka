package com.producto_service.Controller;

import com.producto_service.DTO.ProductoDto;
import com.producto_service.DTO.ProductoResponseDto;
import com.producto_service.Model.Producto;
import com.producto_service.Service.ProductoService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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


//        @GetMapping("/marca/{nombre}")
//        public List<Producto> obtenerPorMarca(@PathVariable String nombre) {
//            return productoService.obtenerProductosPorMarca(nombre);
//        }

        @GetMapping("/lista-ids")
        public List<Producto> obtenerPorIds(@RequestParam List<Long> ids) {
        return productoService.obtenerProductosPorIds(ids);
     }


    @PostMapping()
        public ResponseEntity<?> crearProducto(@Valid @RequestBody ProductoDto producto) {
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

        @DeleteMapping("/eliminar/{nombre}")
        public ResponseEntity<?> eliminarProducto(@PathVariable String nombre) {
            try {
                productoService.eliminarProducto(nombre);
                return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
            } catch (IllegalArgumentException e) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
            }
        }

//    @PutMapping("/actualizar/{id}")
//    public ResponseEntity<?> actualizarProducto(@PathVariable Long id, @RequestBody ProductoDto producto) {
//        try {
//            Producto productoExistente = productoService.obtenerProductoPorId(id);
//            producto.setId(productoExistente.getId());
//            Producto actualizado = productoService.crearProducto(producto);
//            return ResponseEntity.ok(actualizado);
//        } catch (IllegalArgumentException e) {
//            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
//        }

    @PutMapping("/actualizar-stock/{nombre}")
    public ResponseEntity<String> actualizarStock(@PathVariable String nombre , @RequestParam Integer cantidad) {
        try {
            String mensaje = productoService.actualizarStock(nombre, cantidad);
            return ResponseEntity.ok(mensaje);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al actualizar el stock: " + e.getMessage());
        }
    }





}
