package com.gymmanagement.gym_app.service.inventory;

import com.gymmanagement.gym_app.domain.inventory.Venta;
import com.gymmanagement.gym_app.dto.inventory.VentaDTO;
import com.gymmanagement.gym_app.dto.inventory.requests.VentaRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface VentaService {
    Venta crearVenta(VentaRequest ventaRequest);
    Venta anularVenta(Long ventaId, String motivo);
    Venta findById(Long id);
    List<Venta> findByFechas(LocalDateTime fechaInicio, LocalDateTime fechaFin);
    Page<Venta> findAll(Pageable pageable);
    VentaDTO convertToDTO(Venta venta);
    Venta convertToEntity(VentaDTO ventaDTO);
    List<Venta> findByClienteId(UUID clienteId);
    List<Venta> findByUsuarioId(UUID usuarioId);
}
