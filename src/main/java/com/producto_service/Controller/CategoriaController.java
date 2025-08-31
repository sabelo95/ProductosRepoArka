package com.producto_service.Controller;

import com.producto_service.DTO.CategoriaDto;
import com.producto_service.Model.Categoria;
import com.producto_service.Service.CategoriaService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/categorias")
@AllArgsConstructor
public class CategoriaController {

    private final CategoriaService categoriaService;

    @GetMapping("/todas")
    public ResponseEntity<?> obtenerTodasLasCategorias() {
        try {
            List<Categoria> categorias = categoriaService.obtenerTodasLasCategorias();
            return ResponseEntity.ok(categorias);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al obtener las categorías: " + e.getMessage());
        }
    }

    @GetMapping("/{nombre}")
    public ResponseEntity<?> obtenerCategoriaPorNombre(@PathVariable String nombre) {
        try {
            Categoria categoria = categoriaService.obtenerCategoriaPorNombre(nombre);
            if (categoria == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("La categoría con nombre " + nombre + " no existe.");
            }
            return ResponseEntity.ok(categoria);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al obtener la categoría: " + e.getMessage());
        }
    }

    @PostMapping("/crear")
    public ResponseEntity<?> crearCategoria(@RequestBody CategoriaDto categoria) {
        try {
            if (categoria.getNombre() == null || categoria.getNombre().isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("El nombre de la categoría no puede estar vacío.");
            }
            Categoria nuevaCategoria = categoriaService.crearCategoria(categoria);
            return ResponseEntity.status(HttpStatus.CREATED).body(nuevaCategoria);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al crear la categoría: " + e.getMessage());
        }
    }

    @PutMapping("/actualizar")
    public ResponseEntity<?> actualizarCategoria(@RequestBody Categoria categoria) {
        try {
            Categoria actualizada = categoriaService.actualizarCategoria(categoria);
            return ResponseEntity.ok(actualizada);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al actualizar la categoría: " + e.getMessage());
        }
    }

    @DeleteMapping("/eliminar/{nombre}")
    public ResponseEntity<?> eliminarCategoria(@PathVariable String nombre) {
        try {
            categoriaService.eliminarCategoria(nombre);
            return ResponseEntity.ok("Categoría eliminada correctamente.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al eliminar la categoría: " + e.getMessage());
        }
    }

}
