package com.gymmanagement.gym_app.model;


import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class ReportModel {
    private long totalMembers;
    private long renewedMembers;
    private long newMembers;
    private long churnedMembers;
    private double retentionRate;
    private double averageMembershipDurationMonths;
    private long activeMembers;
    private long inactiveMembers;
    private BigDecimal membershipRevenueThisMonth;
    private String mostPopularMembershipPlan;
    private BigDecimal totalDiscountsApplied;
    private BigDecimal netMembershipRevenueThisMonth;
}
