package com.gymmanagement.gym_app.dto.response;

import lombok.Data;
import java.util.UUID;
import java.math.BigDecimal;

@Data
public class PromotionSummaryDTO {
    private UUID id;
    private String name;
    private BigDecimal discountPercentage;
    private BigDecimal totalWithDiscount;

    public void setTotalWithDiscount(BigDecimal totalWithDiscount) {
        this.totalWithDiscount = totalWithDiscount;
    }
    public BigDecimal getTotalWithDiscount() {
        return totalWithDiscount;
    }
}
