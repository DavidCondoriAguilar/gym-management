package com.gymmanagement.gym_app.dto.inventory;

import com.gymmanagement.gym_app.domain.enums.TipoMovimientoInventario;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class InventarioDTO {
    private Long id;
    private Long productoId;
    private String productoNombre;
    private Integer cantidad;
    private TipoMovimientoInventario tipoMovimiento;
    private String descripcion;
    private LocalDateTime fechaMovimiento;
    private Long usuarioRegistroId;
    private String usuarioRegistroNombre;
    private Long ventaId;
}
