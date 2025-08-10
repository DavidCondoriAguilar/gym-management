package com.gymmanagement.gym_app.dto.response;

import lombok.Data;
import java.util.UUID;
import java.math.BigDecimal;

@Data
public class MembershipPlanResponseDTO {
    private UUID id;
    private String name;
    private Integer durationMonths;
    private BigDecimal cost;
    private String description;
    private String type;
}
