package com.gymmanagement.gym_app.domain;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class MembershipRecord {

    @Id
    @GeneratedValue
    private UUID id;

    // Relationship: Each record belongs to a GymMember
    @ManyToOne
    @JoinColumn(name = "gym_member_id")
    private GymMember gymMember;

    @ManyToOne
    @JoinColumn(name = "membership_plan_id")
    private MembershipPlan membershipPlan;

    private LocalDate startDate;
    private LocalDate endDate;

    private String status;
}
