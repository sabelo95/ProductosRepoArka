package com.producto_service.Controller;

import com.producto_service.Model.Marca;
import com.producto_service.Service.MarcaService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
@RequestMapping("marcas")
public class MarcaController {

    private final MarcaService marcaService;

    @GetMapping()
    public ResponseEntity<?> obtenerTodasLasMarcas() {
        try {
            return ResponseEntity.ok(marcaService.obtenerTodasLasMarcas());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al obtener las marcas: " + e.getMessage());
        }
    }

    @GetMapping("{nombre}")
    public ResponseEntity<?> obtenerMarcaPorId(@PathVariable String nombre) {
        try {
            Marca marca = marcaService.obtenerMarcaPorNombre(nombre);
            return ResponseEntity.ok(marca);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al obtener la marca: " + e.getMessage());
        }
    }

    @PostMapping()
    public ResponseEntity<?> crearMarca(@RequestBody Marca marca) {
        try {
            if (marca.getNombre() == null || marca.getNombre().isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("El nombre de la marca no puede estar vacío.");
            }
            Marca nuevaMarca = marcaService.crearMarca(marca);
            return ResponseEntity.status(HttpStatus.CREATED).body(nuevaMarca);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al crear la marca: " + e.getMessage());
        }
    }

    @DeleteMapping("{nombre}")
    public ResponseEntity<?> eliminarMarca(@PathVariable String nombre) {
        try {
            marcaService.eliminarMarca(nombre);
            return ResponseEntity.ok("Marca eliminada exitosamente.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al eliminar la marca: " + e.getMessage());
        }
    }

    @PutMapping("/actualizar/{nombre}")
    public ResponseEntity<?> actualizarMarca(@PathVariable String nombre, @RequestBody Marca marcaActualizada) {
        try {
            Marca marca = marcaService.actualizarMarca(nombre, marcaActualizada);
            return ResponseEntity.ok(marca);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al actualizar la marca: " + e.getMessage());
        }
    }
}

