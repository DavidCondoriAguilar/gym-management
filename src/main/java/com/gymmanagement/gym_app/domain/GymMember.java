package com.gymmanagement.gym_app.domain;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GymMember {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(unique = true, nullable = false, length = 150)
    private String email;

    @Column(length = 9)
    private String phone;

    @Column(nullable = false)
    private Boolean active;

    @Column(nullable = false)
    private LocalDate registrationDate;

    @Column(nullable = false)
    private LocalDate membershipStartDate;

    @Column(nullable = false)
    private LocalDate membershipEndDate;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "membership_plan_id", nullable = false)
    private MembershipPlan membershipPlan;

    @OneToMany(mappedBy = "gymMember", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Payment> payments;

    @OneToMany(mappedBy = "gymMember", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MembershipRecord> membershipRecords;

    @ManyToMany
    @JoinTable(
            name = "gym_member_promotion",
            joinColumns = @JoinColumn(name = "gym_member_id"),
            inverseJoinColumns = @JoinColumn(name = "promotion_id")
    )
    private List<Promotion> promotions;
}
