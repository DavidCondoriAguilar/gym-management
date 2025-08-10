package com.gymmanagement.gym_app.dto.response;

import lombok.Data;
import java.util.UUID;
import java.time.LocalDate;
import com.gymmanagement.gym_app.domain.enums.MembershipStatus;

@Data
public class MembershipRecordSummaryDTO {
    private UUID id;
    private LocalDate startDate;
    private LocalDate endDate;
    private MembershipStatus status;
}
