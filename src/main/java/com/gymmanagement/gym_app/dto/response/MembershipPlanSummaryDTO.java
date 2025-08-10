package com.gymmanagement.gym_app.dto.response;

import lombok.Data;
import java.util.UUID;
import java.math.BigDecimal;

@Data
public class MembershipPlanSummaryDTO {
    private UUID id;
    private String name;
    private Integer durationMonths;
    private BigDecimal cost;
}
