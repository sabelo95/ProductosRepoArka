package com.producto_service.Service;

import com.producto_service.DTO.RequestProductoDto;
import com.producto_service.DTO.ProductoResponseDto;
import com.producto_service.Mapper.ProductoMapper;
import com.producto_service.Model.*;
import com.producto_service.Repository.ProductoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductoServiceTest {

    @Mock
    private ProductoRepository productoRepository;

    @Mock
    private MarcaService marcaService;

    @Mock
    private CategoriaService categoriaService;

    @Mock
    private HistorialService historialService;

    @Mock
    private ProductoMapper productoMapper;

    @InjectMocks
    private ProductoService productoService;

    private Producto producto;
    private Categoria categoria;
    private Marca marca;
    private RequestProductoDto productoDto;

    @BeforeEach
    void setUp() {
        categoria = new Categoria();
        categoria.setId(1L);
        categoria.setNombre("Electrónica");

        marca = new Marca();
        marca.setId(1L);
        marca.setNombre("Samsung");

        producto = new Producto();
        producto.setId(1L);
        producto.setNombre("Producto Test");
        producto.setDescripcion("Descripción Test");
        producto.setPrecio(100.0);
        producto.setCantidad(10);
        producto.setCategoria(categoria);
        producto.setMarca(marca);

        productoDto = new RequestProductoDto();
        productoDto.setNombre("Nuevo Producto");
        productoDto.setDescripcion("Nueva Descripción");
        productoDto.setPrecio(200.0);
        productoDto.setCantidad(5);
        productoDto.setCategoria(categoria);
        productoDto.setMarca(marca);
    }

    @Test
    void obtenerTodosLosProductos_ReturnsList() {
        // Given
        when(productoRepository.findAll()).thenReturn(List.of(producto));

        // When
        List<Producto> result = productoService.obtenerTodosLosProductos();

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(productoRepository).findAll();
    }

    @Test
    void obtenerProductosPorCategoria_ValidCategory_ReturnsProducts() {
        // Given
        String categoriaNombre = "Electrónica";
        when(categoriaService.obtenerCategoriaPorNombre(categoriaNombre)).thenReturn(categoria);
        when(productoRepository.findByCategoriaNombre(categoriaNombre)).thenReturn(List.of(producto));

        // When
        List<Producto> result = productoService.obtenerProductosPorCategoria(categoriaNombre);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(categoriaService).obtenerCategoriaPorNombre(categoriaNombre);
    }

    @Test
    void obtenerProductosPorCategoria_NullCategory_ThrowsException() {
        // When & Then
        assertThrows(IllegalArgumentException.class, () -> 
            productoService.obtenerProductosPorCategoria(null));
    }

    @Test
    void obtenerProductosPorCategoria_EmptyCategory_ThrowsException() {
        // When & Then
        assertThrows(IllegalArgumentException.class, () -> 
            productoService.obtenerProductosPorCategoria(""));
    }

    @Test
    void obtenerProductosPorCategoria_NonExistingCategory_ThrowsException() {
        // Given
        when(categoriaService.obtenerCategoriaPorNombre("Invalid")).thenReturn(null);

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> 
            productoService.obtenerProductosPorCategoria("Invalid"));
    }

    @Test
    void obtenerProductosPorMarca_ValidMarca_ReturnsProductNames() {
        // Given
        String marcaNombre = "Samsung";
        when(marcaService.obtenerMarcaPorNombre(marcaNombre)).thenReturn(marca);
        when(productoRepository.findByMarcaNombre(marcaNombre)).thenReturn(List.of(producto));

        // When
        List<String> result = productoService.obtenerProductosPorMarca(marcaNombre);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Producto Test", result.get(0));
    }

    @Test
    void obtenerProductosPorMarca_NullMarca_ThrowsException() {
        // When & Then
        assertThrows(IllegalArgumentException.class, () -> 
            productoService.obtenerProductosPorMarca(null));
    }

    @Test
    void obtenerProductoPorNombre_ExistingProduct_ReturnsProduct() {
        // Given
        when(productoRepository.findByNombre("Producto Test")).thenReturn(Optional.of(producto));

        // When
        Producto result = productoService.obtenerProductoPorNombre("Producto Test");

        // Then
        assertNotNull(result);
        assertEquals("Producto Test", result.getNombre());
    }

    @Test
    void obtenerProductoPorNombre_NonExistingProduct_ReturnsNull() {
        // Given
        when(productoRepository.findByNombre("No existe")).thenReturn(Optional.empty());

        // When
        Producto result = productoService.obtenerProductoPorNombre("No existe");

        // Then
        assertNull(result);
    }

    @Test
    void crearProducto_ValidProduct_CreatesProduct() {
        // Given
        ProductoResponseDto responseDto = new ProductoResponseDto();
        responseDto.setNombre("Nuevo Producto");
        
        when(productoRepository.findByNombre(productoDto.getNombre())).thenReturn(Optional.empty());
        when(categoriaService.validarCategoria(categoria)).thenReturn(true);
        when(marcaService.validarMarca(marca.getNombre())).thenReturn(true);
        when(productoRepository.save(any(Producto.class))).thenReturn(producto);
        when(productoMapper.toDto(producto)).thenReturn(responseDto);
        doNothing().when(historialService).agregarHistorial(any(Producto.class), anyInt());

        // When
        ProductoResponseDto result = productoService.crearProducto(productoDto);

        // Then
        assertNotNull(result);
        verify(productoRepository).save(any(Producto.class));
        verify(historialService).agregarHistorial(any(Producto.class), anyInt());
    }

    @Test
    void crearProducto_DuplicateName_ThrowsException() {
        // Given
        when(productoRepository.findByNombre(productoDto.getNombre())).thenReturn(Optional.of(producto));

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> 
            productoService.crearProducto(productoDto));
    }

    @Test
    void crearProducto_NegativeQuantity_ThrowsException() {
        // Given
        productoDto.setCantidad(-1);
        when(productoRepository.findByNombre(productoDto.getNombre())).thenReturn(Optional.empty());

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> 
            productoService.crearProducto(productoDto));
    }

    @Test
    void crearProducto_InvalidCategory_ThrowsException() {
        // Given
        when(productoRepository.findByNombre(productoDto.getNombre())).thenReturn(Optional.empty());
        when(categoriaService.validarCategoria(categoria)).thenReturn(false);

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> 
            productoService.crearProducto(productoDto));
    }

    @Test
    void eliminarProducto_ExistingProduct_DeletesProduct() {
        // Given
        when(productoRepository.findByNombre("Producto Test")).thenReturn(Optional.of(producto));

        // When
        productoService.eliminarProducto("Producto Test");

        // Then
        verify(productoRepository).delete(producto);
    }

    @Test
    void eliminarProducto_NonExistingProduct_ThrowsException() {
        // Given
        when(productoRepository.findByNombre("No existe")).thenReturn(Optional.empty());

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> 
            productoService.eliminarProducto("No existe"));
    }

    @Test
    void reduccionStock_ValidProduct_ReducesStock() {
        // Given
        Map<Long, Integer> productos = Map.of(1L, 3);
        when(productoRepository.findById(1L)).thenReturn(Optional.of(producto));
        when(productoRepository.save(any(Producto.class))).thenReturn(producto);
        doNothing().when(historialService).agregarHistorial(any(Producto.class), anyInt());

        // When
        productoService.reduccionStock(productos);

        // Then
        assertEquals(7, producto.getCantidad());
        verify(historialService).agregarHistorial(any(Producto.class), anyInt());
        verify(productoRepository).save(producto);
    }

    @Test
    void reduccionStock_InsufficientStock_ThrowsException() {
        // Given
        Map<Long, Integer> productos = Map.of(1L, 20);
        when(productoRepository.findById(1L)).thenReturn(Optional.of(producto));

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> 
            productoService.reduccionStock(productos));
    }

    @Test
    void reposicionStock_ValidProduct_IncreasesStock() {
        // Given
        Map<Long, Integer> productos = Map.of(1L, 5);
        when(productoRepository.findById(1L)).thenReturn(Optional.of(producto));
        when(productoRepository.save(any(Producto.class))).thenReturn(producto);
        doNothing().when(historialService).agregarHistorial(any(Producto.class), anyInt());

        // When
        productoService.reposicionStock(productos);

        // Then
        assertEquals(15, producto.getCantidad());
        verify(historialService).agregarHistorial(any(Producto.class), anyInt());
        verify(productoRepository).save(producto);
    }

    @Test
    void obtenerProductosPorIds_ValidIds_ReturnsProducts() {
        // Given
        List<Long> ids = List.of(1L);
        when(productoRepository.findAllById(ids)).thenReturn(List.of(producto));

        // When
        List<Producto> result = productoService.obtenerProductosPorIds(ids);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        // El método llama a findAllById dos veces: una para validar y otra para retornar
        verify(productoRepository, times(2)).findAllById(ids);
    }

    @Test
    void obtenerProductosPorIds_NullIds_ThrowsException() {
        // When & Then
        assertThrows(IllegalArgumentException.class, () -> 
            productoService.obtenerProductosPorIds(null));
    }

    @Test
    void obtenerProductosConStockMenorA_ReturnsFilteredProducts() {
        // Given
        when(productoRepository.findAll()).thenReturn(List.of(producto));

        // When
        List<Producto> result = productoService.obtenerProductosConStockMenorA(15);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
    }
}


