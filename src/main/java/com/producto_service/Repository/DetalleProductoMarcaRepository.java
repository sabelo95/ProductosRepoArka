package com.producto_service.Repository;

import com.producto_service.Model.DetalleProductoMarca;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DetalleProductoMarcaRepository extends JpaRepository<DetalleProductoMarca, Long> {

    Optional<DetalleProductoMarca> findByProducto_IdAndMarca_Id(Long productoId, Long marcaId);

}

