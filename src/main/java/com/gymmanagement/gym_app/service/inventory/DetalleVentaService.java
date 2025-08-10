package com.gymmanagement.gym_app.service.inventory;

import com.gymmanagement.gym_app.domain.inventory.DetalleVenta;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface DetalleVentaService {
    List<DetalleVenta> findAll();
    Page<DetalleVenta> findAll(Pageable pageable);
    Optional<DetalleVenta> findById(Long id);
    DetalleVenta save(DetalleVenta detalleVenta);
    void deleteById(Long id);
    List<DetalleVenta> findByVentaId(Long ventaId);
    List<DetalleVenta> findByProductoId(Long productoId);
}
