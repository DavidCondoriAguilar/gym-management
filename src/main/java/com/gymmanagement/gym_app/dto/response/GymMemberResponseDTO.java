package com.gymmanagement.gym_app.dto.response;

import lombok.Data;
import java.util.List;
import java.util.UUID;
import java.time.LocalDate;

@Data
public class GymMemberResponseDTO {
    private UUID id;
    private String name;
    private String email;
    private String phone;
    private Boolean active;
    private LocalDate registrationDate;
    private LocalDate membershipStartDate;
    private LocalDate membershipEndDate;
    private MembershipPlanSummaryDTO membershipPlan;
    private List<PaymentSummaryDTO> payments;
    private List<PromotionSummaryDTO> promotions;
    private List<MembershipRecordSummaryDTO> membershipRecords;
}
