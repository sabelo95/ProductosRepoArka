package com.producto_service.Controller;

import com.producto_service.DTO.DetalleProductoMarcaRequestDto;
import com.producto_service.Service.DetalleProductoMarcaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/detalle-producto-marca")
@RequiredArgsConstructor
public class DetalleProductoMarcaController {

    private final DetalleProductoMarcaService detalleProductoMarcaService;

    @PutMapping()
    public ResponseEntity<String> actualizarStock(@RequestBody  DetalleProductoMarcaRequestDto request) {
        try {
            detalleProductoMarcaService.ActualizarStock(request.getProductoNombre(), request.getMarcaNombre(), request.getCantidad());
            return ResponseEntity.ok("Stock actualizado correctamente");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error interno del servidor");
        }
    }

}

