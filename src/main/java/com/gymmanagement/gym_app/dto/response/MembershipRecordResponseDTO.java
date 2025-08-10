package com.gymmanagement.gym_app.dto.response;

import lombok.Data;
import java.util.UUID;
import java.time.LocalDate;
import com.gymmanagement.gym_app.domain.enums.MembershipStatus;

@Data
public class MembershipRecordResponseDTO {
    private UUID id;
    private UUID memberId;
    private UUID planId;
    private LocalDate startDate;
    private LocalDate endDate;
    private MembershipStatus status;
    private LocalDate cancellationDate;
    private String notes;
}
