package com.gymmanagement.gym_app.dto.response;

import lombok.Data;
import java.math.BigDecimal;
import java.util.UUID;

/**
 * DTO que representa el resumen de una promoción, incluyendo información sobre descuentos
 * y montos totales.
 */
@Data
public class PromotionSummaryDTO {
    /**
     * ID único de la promoción
     */
    private UUID id;
    
    /**
     * Nombre de la promoción
     */
    private String name;
    
    /**
     * Porcentaje de descuento (ej: 30.00 para 30%)
     */
    private BigDecimal discountPercentage;
    
    /**
     * Costo total original (sin descuento) de los planes de los miembros
     */
    private BigDecimal totalOriginal = BigDecimal.ZERO;
    
    /**
     * Monto total del descuento aplicado
     */
    private BigDecimal totalDiscount = BigDecimal.ZERO;
    
    /**
     * Costo total con el descuento aplicado
     */
    private BigDecimal totalWithDiscount = BigDecimal.ZERO;
    
    /**
     * Número de miembros que tienen esta promoción
     */
    private Integer memberCount = 0;
    
    /**
     * Establece el porcentaje de descuento, asegurando que no sea nulo
     */
    public void setDiscountPercentage(BigDecimal discountPercentage) {
        this.discountPercentage = discountPercentage != null ? discountPercentage : BigDecimal.ZERO;
    }
    
    /**
     * Establece el total original, asegurando que no sea nulo
     */
    public void setTotalOriginal(BigDecimal totalOriginal) {
        this.totalOriginal = totalOriginal != null ? totalOriginal : BigDecimal.ZERO;
    }
    
    /**
     * Establece el descuento total, asegurando que no sea nulo
     */
    public void setTotalDiscount(BigDecimal totalDiscount) {
        this.totalDiscount = totalDiscount != null ? totalDiscount : BigDecimal.ZERO;
    }
    
    /**
     * Establece el total con descuento, asegurando que no sea nulo
     */
    public void setTotalWithDiscount(BigDecimal totalWithDiscount) {
        this.totalWithDiscount = totalWithDiscount != null ? totalWithDiscount : BigDecimal.ZERO;
    }
}
