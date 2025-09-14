package com.producto_service.Service;

import com.producto_service.DTO.CategoriaDto;
import com.producto_service.Model.Categoria;
import com.producto_service.Repository.CategoriaRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class CategoriaService {

    private final CategoriaRepository categoriaRepository;

    public List<Categoria> obtenerTodasLasCategorias() {
        return categoriaRepository.findAll();
    }

    public Categoria crearCategoria(CategoriaDto categoriaDto) {
        if (categoriaDto.getNombre() == null || categoriaDto.getNombre().isEmpty()) {
            throw new IllegalArgumentException("El nombre de la categoría no puede estar vacío.");
        }

        if (categoriaRepository.findByNombre(categoriaDto.getNombre()) != null) {
            throw new IllegalArgumentException("La categoría con nombre " + categoriaDto.getNombre() + " ya existe.");
        }

        Categoria categoriasaved = new Categoria();
        categoriasaved.setNombre(categoriaDto.getNombre());
        categoriasaved.setDescripcion(categoriaDto.getDescripcion());
        categoriasaved.setProductos(categoriaDto.getProductos());
        return categoriaRepository.save(categoriasaved);
    }

    public Categoria obtenerCategoriaPorNombre(String nombre) {
        return categoriaRepository.findByNombre(nombre);

    }

    public void eliminarCategoria(String nombre) {
        Categoria categoria = obtenerCategoriaPorNombre(nombre);
        if (categoria != null) {
            categoriaRepository.delete(categoria);
        } else {
            throw new IllegalArgumentException("La categoría con nombre " + nombre + " no existe.");
        }
    }

    public Categoria actualizarCategoria(Categoria categoria) {
        if (categoria.getId() == null || !categoriaRepository.existsById(categoria.getId())) {
            throw new IllegalArgumentException("La categoría con ID " + categoria.getId() + " no existe.");
        }
        return categoriaRepository.save(categoria);
    }

    public boolean validarCategoria(Categoria categoria) {
        List<Categoria> categorias = obtenerTodasLasCategorias();
        return categorias.stream().anyMatch(c -> c.getId().equals(categoria.getId()));
    }

}
