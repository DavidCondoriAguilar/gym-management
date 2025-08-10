package com.gymmanagement.gym_app.service.inventory;

import com.gymmanagement.gym_app.domain.inventory.Inventario;
import com.gymmanagement.gym_app.domain.inventory.Producto;
import com.gymmanagement.gym_app.domain.enums.TipoMovimientoInventario;
import com.gymmanagement.gym_app.dto.inventory.requests.MovimientoInventarioRequest;

import java.time.LocalDateTime;
import java.util.List;

public interface InventarioService {
    Inventario registrarMovimiento(Inventario movimiento);
    List<Inventario> obtenerMovimientosPorProducto(Producto producto);
    List<Inventario> obtenerMovimientosPorRangoFechas(LocalDateTime fechaInicio, LocalDateTime fechaFin);
    Integer obtenerStockActual(Producto producto);
    void registrarEntrada(Producto producto, int cantidad, String descripcion, Long usuarioId);
    void registrarSalida(Producto producto, int cantidad, String descripcion, Long usuarioId, Long ventaId);
    Inventario registrarMovimiento(MovimientoInventarioRequest request);
}
