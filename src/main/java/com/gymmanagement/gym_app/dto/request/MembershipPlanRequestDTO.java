package com.gymmanagement.gym_app.dto.request;

import lombok.Data;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;

@Data
public class MembershipPlanRequestDTO {
    @NotBlank
    @Size(max = 100)
    private String name;

    @NotNull
    @Min(1)
    private Integer durationMonths;

    @NotNull
    @DecimalMin(value = "0.0", inclusive = false)
    private BigDecimal cost;

    @Size(max = 500)
    private String description;

    @NotBlank
    private String type;
}
