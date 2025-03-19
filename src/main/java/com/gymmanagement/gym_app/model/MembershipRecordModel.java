package com.gymmanagement.gym_app.model;

import lombok.*;
import java.time.LocalDate;
import java.util.UUID;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MembershipRecordModel {
    private UUID id;
    private UUID gymMemberId;
    private UUID membershipPlanId;
    private LocalDate startDate;
    private LocalDate endDate;
    private String status;
    private LocalDate cancellationDate;
}
