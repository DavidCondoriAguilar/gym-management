package com.gymmanagement.gym_app.model;

import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PromotionModel {
    private UUID id;
    private String name;
    private BigDecimal discountPercentage;
    private LocalDate startDate;
    private LocalDate endDate;
    private List<UUID> gymMemberIds; // IDs de miembros con esta promoción
    private BigDecimal totalWithDiscount;

}
