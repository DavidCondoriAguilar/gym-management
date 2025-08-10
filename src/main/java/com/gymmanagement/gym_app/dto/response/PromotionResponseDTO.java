package com.gymmanagement.gym_app.dto.response;

import lombok.Data;
import java.util.UUID;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
public class PromotionResponseDTO {
    private UUID id;
    private String name;
    private BigDecimal discountPercentage;
    private LocalDate startDate;
    private LocalDate endDate;
    private Integer maxUses;
    private Integer usageCount;
    private List<UUID> memberIds;
    private String description;
}
