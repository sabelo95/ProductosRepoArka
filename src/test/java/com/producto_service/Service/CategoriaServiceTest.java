package com.producto_service.Service;

import com.producto_service.DTO.CategoriaDto;
import com.producto_service.Model.Categoria;
import com.producto_service.Repository.CategoriaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CategoriaServiceTest {

    @Mock
    private CategoriaRepository categoriaRepository;

    @InjectMocks
    private CategoriaService categoriaService;

    private Categoria categoria;
    private CategoriaDto categoriaDto;

    @BeforeEach
    void setUp() {
        categoria = new Categoria();
        categoria.setId(1L);
        categoria.setNombre("Electrónica");
        categoria.setDescripcion("Productos electrónicos");
        categoria.setProductos(new ArrayList<>());

        categoriaDto = new CategoriaDto();
        categoriaDto.setNombre("Electrónica");
        categoriaDto.setDescripcion("Productos electrónicos");
    }

    // =========================================================================
    // TESTS DE CONSULTA
    // =========================================================================

    @Test
    void obtenerTodasLasCategorias_ReturnsList() {
        // Given
        when(categoriaRepository.findAll()).thenReturn(List.of(categoria));

        // When
        List<Categoria> result = categoriaService.obtenerTodasLasCategorias();

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(categoriaRepository).findAll();
    }

    @Test
    void obtenerCategoriaPorNombre_ExistingCategoria_ReturnsCategoria() {
        // Given
        when(categoriaRepository.findByNombre("Electrónica")).thenReturn(categoria);

        // When
        Categoria result = categoriaService.obtenerCategoriaPorNombre("Electrónica");

        // Then
        assertNotNull(result);
        assertEquals("Electrónica", result.getNombre());
        verify(categoriaRepository).findByNombre("Electrónica");
    }

    @Test
    void obtenerCategoriaPorId_ExistingCategoria_ReturnsCategoria() {
        // Given
        when(categoriaRepository.findById(1L)).thenReturn(Optional.of(categoria));

        // When
        Categoria result = categoriaService.obtenerCategoriaPorId(1L);

        // Then
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Electrónica", result.getNombre());
        verify(categoriaRepository).findById(1L);
    }

    @Test
    void obtenerCategoriaPorId_NonExisting_ReturnsNull() {
        // Given
        when(categoriaRepository.findById(999L)).thenReturn(Optional.empty());

        // When
        Categoria result = categoriaService.obtenerCategoriaPorId(999L);

        // Then
        assertNull(result);
        verify(categoriaRepository).findById(999L);
    }

    // =========================================================================
    // TESTS DE CREACIÓN
    // =========================================================================

    @Test
    void crearCategoria_ValidCategoria_CreatesCategoria() {
        // Given
        when(categoriaRepository.findByNombre(categoriaDto.getNombre())).thenReturn(null);
        when(categoriaRepository.save(any(Categoria.class))).thenReturn(categoria);

        // When
        Categoria result = categoriaService.crearCategoria(categoriaDto);

        // Then
        assertNotNull(result);
        verify(categoriaRepository).findByNombre(categoriaDto.getNombre());
        verify(categoriaRepository).save(any(Categoria.class));
    }

    @Test
    void crearCategoria_NullName_ThrowsException() {
        // Given
        categoriaDto.setNombre(null);

        // When & Then
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> categoriaService.crearCategoria(categoriaDto)
        );

        assertEquals("El nombre de la categoría no puede estar vacío", exception.getMessage());
        verify(categoriaRepository, never()).save(any());
    }

    @Test
    void crearCategoria_EmptyName_ThrowsException() {
        // Given
        categoriaDto.setNombre("");

        // When & Then
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> categoriaService.crearCategoria(categoriaDto)
        );

        assertEquals("El nombre de la categoría no puede estar vacío", exception.getMessage());
        verify(categoriaRepository, never()).save(any());
    }

    @Test
    void crearCategoria_WhitespaceName_ThrowsException() {
        // Given
        categoriaDto.setNombre("   ");

        // When & Then
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> categoriaService.crearCategoria(categoriaDto)
        );

        assertEquals("El nombre de la categoría no puede estar vacío", exception.getMessage());
    }

    @Test
    void crearCategoria_DuplicateName_ThrowsException() {
        // Given
        when(categoriaRepository.findByNombre(categoriaDto.getNombre())).thenReturn(categoria);

        // When & Then
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> categoriaService.crearCategoria(categoriaDto)
        );

        assertTrue(exception.getMessage().contains("Ya existe una categoría con el nombre"));
        verify(categoriaRepository, never()).save(any());
    }

    // =========================================================================
    // TESTS DE ACTUALIZACIÓN
    // =========================================================================

    @Test
    void actualizarCategoria_ValidUpdate_UpdatesCategoria() {
        // Given
        CategoriaDto updateDto = new CategoriaDto();
        updateDto.setNombre("Electrónica Actualizada");
        updateDto.setDescripcion("Nueva descripción");

        when(categoriaRepository.findById(1L)).thenReturn(Optional.of(categoria));
        when(categoriaRepository.findByNombre("Electrónica Actualizada")).thenReturn(null);
        when(categoriaRepository.save(any(Categoria.class))).thenReturn(categoria);

        // When
        Categoria result = categoriaService.actualizarCategoria(1L, updateDto);

        // Then
        assertNotNull(result);
        verify(categoriaRepository).findById(1L);
        verify(categoriaRepository).findByNombre("Electrónica Actualizada");
        verify(categoriaRepository).save(any(Categoria.class));
    }

    @Test
    void actualizarCategoria_NonExistingId_ThrowsException() {
        // Given
        when(categoriaRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> categoriaService.actualizarCategoria(999L, categoriaDto)
        );

        assertTrue(exception.getMessage().contains("No existe una categoría con el ID"));
        verify(categoriaRepository, never()).save(any());
    }

    @Test
    void actualizarCategoria_EmptyName_ThrowsException() {
        // Given
        categoriaDto.setNombre("");
        when(categoriaRepository.findById(1L)).thenReturn(Optional.of(categoria));

        // When & Then
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> categoriaService.actualizarCategoria(1L, categoriaDto)
        );

        assertEquals("El nombre de la categoría no puede estar vacío", exception.getMessage());
        verify(categoriaRepository, never()).save(any());
    }

    @Test
    void actualizarCategoria_DuplicateNameInOtherCategory_ThrowsException() {
        // Given
        Categoria otraCategoria = new Categoria();
        otraCategoria.setId(2L);
        otraCategoria.setNombre("Ropa");

        CategoriaDto updateDto = new CategoriaDto();
        updateDto.setNombre("Ropa");
        updateDto.setDescripcion("Intentando duplicar");

        when(categoriaRepository.findById(1L)).thenReturn(Optional.of(categoria));
        when(categoriaRepository.findByNombre("Ropa")).thenReturn(otraCategoria);

        // When & Then
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> categoriaService.actualizarCategoria(1L, updateDto)
        );

        assertTrue(exception.getMessage().contains("Ya existe otra categoría con el nombre"));
        verify(categoriaRepository, never()).save(any());
    }

    @Test
    void actualizarCategoria_SameNameSameCategory_AllowsUpdate() {
        // Given
        CategoriaDto updateDto = new CategoriaDto();
        updateDto.setNombre("Electrónica"); // Mismo nombre
        updateDto.setDescripcion("Nueva descripción");

        when(categoriaRepository.findById(1L)).thenReturn(Optional.of(categoria));
        when(categoriaRepository.save(any(Categoria.class))).thenReturn(categoria);

        // When
        Categoria result = categoriaService.actualizarCategoria(1L, updateDto);

        // Then
        assertNotNull(result);
        verify(categoriaRepository).save(any(Categoria.class));
    }

    // =========================================================================
    // TESTS DE ELIMINACIÓN
    // =========================================================================

    @Test
    void eliminarCategoriaPorId_ExistingCategoriaWithoutProducts_DeletesCategoria() {
        // Given
        when(categoriaRepository.findById(1L)).thenReturn(Optional.of(categoria));

        // When
        categoriaService.eliminarCategoriaPorId(1L);

        // Then
        verify(categoriaRepository).findById(1L);
        verify(categoriaRepository).delete(categoria);
    }

    @Test
    void eliminarCategoriaPorId_NonExisting_ThrowsException() {
        // Given
        when(categoriaRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> categoriaService.eliminarCategoriaPorId(999L)
        );

        assertTrue(exception.getMessage().contains("No existe una categoría con el ID"));
        verify(categoriaRepository, never()).delete(any());
    }

    @Test
    void eliminarCategoriaPorNombre_ExistingCategoria_DeletesCategoria() {
        // Given
        when(categoriaRepository.findByNombre("Electrónica")).thenReturn(categoria);

        // When
        categoriaService.eliminarCategoriaPorNombre("Electrónica");

        // Then
        verify(categoriaRepository).findByNombre("Electrónica");
        verify(categoriaRepository).delete(categoria);
    }

    @Test
    void eliminarCategoriaPorNombre_NonExisting_ThrowsException() {
        // Given
        when(categoriaRepository.findByNombre("No existe")).thenReturn(null);

        // When & Then
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> categoriaService.eliminarCategoriaPorNombre("No existe")
        );

        assertTrue(exception.getMessage().contains("No existe una categoría con el nombre"));
        verify(categoriaRepository, never()).delete(any());
    }

    // =========================================================================
    // TESTS DE VALIDACIÓN
    // =========================================================================

    @Test
    void existeCategoria_ExistingId_ReturnsTrue() {
        // Given
        when(categoriaRepository.existsById(1L)).thenReturn(true);

        // When
        boolean result = categoriaService.existeCategoria(1L);

        // Then
        assertTrue(result);
        verify(categoriaRepository).existsById(1L);
    }

    @Test
    void existeCategoria_NonExistingId_ReturnsFalse() {
        // Given
        when(categoriaRepository.existsById(999L)).thenReturn(false);

        // When
        boolean result = categoriaService.existeCategoria(999L);

        // Then
        assertFalse(result);
        verify(categoriaRepository).existsById(999L);
    }

    @Test
    void existeCategoriaPorNombre_ExistingName_ReturnsTrue() {
        // Given
        when(categoriaRepository.findByNombre("Electrónica")).thenReturn(categoria);

        // When
        boolean result = categoriaService.existeCategoriaPorNombre("Electrónica");

        // Then
        assertTrue(result);
        verify(categoriaRepository).findByNombre("Electrónica");
    }

    @Test
    void existeCategoriaPorNombre_NonExistingName_ReturnsFalse() {
        // Given
        when(categoriaRepository.findByNombre("No existe")).thenReturn(null);

        // When
        boolean result = categoriaService.existeCategoriaPorNombre("No existe");

        // Then
        assertFalse(result);
        verify(categoriaRepository).findByNombre("No existe");
    }

    // =========================================================================
    // TESTS DE MÉTODOS DEPRECATED
    // =========================================================================

    @Test
    @SuppressWarnings("deprecation")
    void actualizarCategoria_Deprecated_ValidCategoria_UpdatesCategoria() {
        // Given
        when(categoriaRepository.existsById(1L)).thenReturn(true);
        when(categoriaRepository.save(categoria)).thenReturn(categoria);

        // When
        Categoria result = categoriaService.actualizarCategoria(categoria);

        // Then
        assertNotNull(result);
        verify(categoriaRepository).existsById(1L);
        verify(categoriaRepository).save(categoria);
    }

    @Test
    @SuppressWarnings("deprecation")
    void actualizarCategoria_Deprecated_InvalidId_ThrowsException() {
        // Given
        categoria.setId(999L);
        when(categoriaRepository.existsById(999L)).thenReturn(false);

        // When & Then
        assertThrows(IllegalArgumentException.class,
                () -> categoriaService.actualizarCategoria(categoria));
        verify(categoriaRepository, never()).save(any());
    }

    @Test
    @SuppressWarnings("deprecation")
    void validarCategoria_Deprecated_ValidCategoria_ReturnsTrue() {
        // Given
        when(categoriaRepository.existsById(1L)).thenReturn(true);

        // When
        boolean result = categoriaService.validarCategoria(categoria);

        // Then
        assertTrue(result);
    }

    @Test
    @SuppressWarnings("deprecation")
    void validarCategoria_Deprecated_NullCategoria_ReturnsFalse() {
        // When
        boolean result = categoriaService.validarCategoria(null);

        // Then
        assertFalse(result);
        verify(categoriaRepository, never()).existsById(any());
    }
}