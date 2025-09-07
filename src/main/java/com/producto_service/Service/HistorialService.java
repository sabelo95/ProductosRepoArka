package com.producto_service.Service;

import com.producto_service.Model.DetalleProductoMarca;
import com.producto_service.Model.Historial;
import com.producto_service.Model.Producto;
import com.producto_service.Repository.HistorialRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class HistorialService {

    private final HistorialRepository historialRepository;

    public void agregarHistorial(DetalleProductoMarca detalleProductoMarca) {
        Historial historial = new Historial();
        historial.setDetalleProductoMarca(detalleProductoMarca);
        historial.setStock_cambiado(detalleProductoMarca.getCantidad());
        historialRepository.save(historial);
    }
}
