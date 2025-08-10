package com.gymmanagement.gym_app.dto.request;

import lombok.Data;
import jakarta.validation.constraints.*;
import java.util.UUID;
import java.time.LocalDate;
import com.gymmanagement.gym_app.domain.enums.MembershipStatus;

@Data
public class MembershipRecordRequestDTO {
    @NotNull
    private UUID memberId;
    @NotNull
    private UUID planId;
    @NotNull
    private LocalDate startDate;
    @NotNull
    private LocalDate endDate;
    @NotNull
    private MembershipStatus status;
    private LocalDate cancellationDate;
    @Size(max = 400)
    private String notes;
}
