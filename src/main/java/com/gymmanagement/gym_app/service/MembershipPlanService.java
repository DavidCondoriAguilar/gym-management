package com.gymmanagement.gym_app.service;

import com.gymmanagement.gym_app.model.MembershipPlanModel;
import java.util.List;
import java.util.UUID;

public interface MembershipPlanService {
    MembershipPlanModel createMembershipPlan(MembershipPlanModel membershipPlanModel);
    MembershipPlanModel getMembershipPlanById(UUID id);
    List<MembershipPlanModel> getAllMembershipPlans();
    MembershipPlanModel updateMembershipPlan(UUID id, MembershipPlanModel membershipPlanModel);
    void deleteMembershipPlan(UUID id);
}
