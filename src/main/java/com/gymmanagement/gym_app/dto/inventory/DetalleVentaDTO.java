package com.gymmanagement.gym_app.dto.inventory;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class DetalleVentaDTO {
    private Long id;
    private Long productoId;
    private String productoNombre;
    private String productoCodigo;
    private Integer cantidad;
    private BigDecimal precioUnitario;
    private BigDecimal descuento;
    private BigDecimal subtotal;
}
