package com.gymmanagement.gym_app.model;

import lombok.*;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class GymMemberModel {

    private UUID id;
    private String name;
    private String email;
    private String phone;
    private Boolean active;
    private LocalDate registrationDate;
    private LocalDate membershipStart;
    private LocalDate membershipEnd;

    private MembershipPlanModel membershipPlan;
    private List<PaymentModel> payments;
    private List<MembershipRecordModel> membershipRecords;
    private List<PromotionModel> promotions;
}
