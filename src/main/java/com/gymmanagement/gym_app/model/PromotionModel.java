package com.gymmanagement.gym_app.model;

import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class PromotionModel {

    private UUID id;
    private String name;
    private BigDecimal discountPercentage;
    private LocalDate startDate;
    private LocalDate endDate;
}
