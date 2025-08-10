package com.gymmanagement.gym_app.dto.request;

import lombok.Data;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Data
public class PromotionRequestDTO {
    @NotBlank
    @Size(max = 100)
    private String name;
    @NotNull
    @DecimalMin("0.0")
    private BigDecimal discountPercentage;
    @NotNull
    private LocalDate startDate;
    @NotNull
    private LocalDate endDate;
    private Integer maxUses;
    private List<UUID> memberIds;
    private String description;
}
