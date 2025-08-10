package com.gymmanagement.gym_app.domain;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Promotion {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(nullable = false)
    private String name;

    @Column(precision = 5, scale = 2)
    private BigDecimal discountPercentage;

    private LocalDate startDate;
    private LocalDate endDate;

    @Column(name = "max_uses")
    private Integer maxUses;

    @Column(name = "usage_count")
    private Integer usageCount;

    @Column(length = 400)
    private String description;

    @ManyToMany(mappedBy = "promotions")
    private List<GymMember> gymMembers;
}
