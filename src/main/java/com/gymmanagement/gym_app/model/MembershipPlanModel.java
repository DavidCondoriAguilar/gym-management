package com.gymmanagement.gym_app.model;

import com.gymmanagement.gym_app.domain.enums.MembershipType;
import lombok.*;
import java.math.BigDecimal;
import java.util.UUID;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class MembershipPlanModel {

    private UUID id;
    private String name;
    private Integer durationMonths;
    private BigDecimal cost;
    private String description;
    private MembershipType type;
}
