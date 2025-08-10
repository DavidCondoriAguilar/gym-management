package com.gymmanagement.gym_app.service;

import com.gymmanagement.gym_app.dto.request.MembershipPlanRequestDTO;
import com.gymmanagement.gym_app.dto.response.MembershipPlanResponseDTO;
import java.util.List;
import java.util.UUID;

public interface MembershipPlanService {
    MembershipPlanResponseDTO createPlan(MembershipPlanRequestDTO requestDTO);
    MembershipPlanResponseDTO getPlanById(UUID id);
    List<MembershipPlanResponseDTO> getAllPlans();
    MembershipPlanResponseDTO updatePlan(UUID id, MembershipPlanRequestDTO requestDTO);
    void deletePlan(UUID id);
}
