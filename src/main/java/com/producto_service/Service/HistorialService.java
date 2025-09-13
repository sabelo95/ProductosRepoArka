package com.producto_service.Service;

import com.producto_service.Model.Historial;
import com.producto_service.Model.Producto;
import com.producto_service.Repository.HistorialRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class HistorialService {

    private final HistorialRepository historialRepository;

    public void agregarHistorial(Producto producto) {
        Historial historial = new Historial();
        historial.setProducto(producto);
        historial.setStock_cambiado(producto.getCantidad());
        historialRepository.save(historial);
    }
}
