package com.producto_service.Service;

import com.producto_service.Model.Marca;
import com.producto_service.Repository.MarcaRepository;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Data
@AllArgsConstructor
public class MarcaService {

    private final MarcaRepository marcaRepository;

    public List<Marca> obtenerTodasLasMarcas() {
        return marcaRepository.findAll();
    }

    public Marca obtenerMarcaPorNombre(String nombre) {
        return marcaRepository.findByNombre(nombre)
                .orElseThrow(() -> new IllegalArgumentException("Marca no encontrada con nombre: " + nombre));
    }

    public Marca crearMarca(Marca marca) {
        return marcaRepository.save(marca);
    }

    public void eliminarMarca(String nombre) {
        Marca marca = obtenerMarcaPorNombre(nombre);
        marcaRepository.delete(marca);
    }

    public Marca actualizarMarca(String nombre, Marca marcaActualizada) {
        Marca marcaExistente = obtenerMarcaPorNombre(nombre);
        marcaExistente.setNombre(marcaActualizada.getNombre());
        return marcaRepository.save(marcaExistente);
    }

    public String validarMarca(List<Marca> marcas) {

        List<Long> ids = obtenerTodasLasMarcas().stream().map(marca -> marca.getId()).collect(Collectors.toList());
        for (Marca marca : marcas) {
            if (!ids.contains(marca.getId())) {
                throw  new IllegalArgumentException("La marca con ID " + marca.getId() + " no es válida, debe crearla primero.");
            }
        }
        return "Todas las marcas son válidas.";
    }


}
