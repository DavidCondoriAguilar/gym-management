package com.gymmanagement.gym_app.service.serviceImpl;

import com.gymmanagement.gym_app.domain.MembershipPlan;
import com.gymmanagement.gym_app.domain.enums.MembershipType;
import com.gymmanagement.gym_app.exception.ResourceNotFoundException;
import com.gymmanagement.gym_app.mapper.MembershipPlanMapper;
import com.gymmanagement.gym_app.model.MembershipPlanModel;
import com.gymmanagement.gym_app.repository.MembershipPlanRepository;
import com.gymmanagement.gym_app.service.MembershipPlanService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class MembershipPlanServiceImpl implements MembershipPlanService {

    private final MembershipPlanRepository membershipPlanRepository;
    private final MembershipPlanMapper membershipPlanMapper;
    @Override
    public MembershipPlanModel createMembershipPlan(MembershipPlanModel membershipPlanModel) {
        if (membershipPlanModel.getType() == null) {
            membershipPlanModel.setType(MembershipType.STANDARD);
        }

        MembershipPlan membershipPlan = membershipPlanMapper.toEntity(membershipPlanModel);
        membershipPlan = membershipPlanRepository.save(membershipPlan);
        return membershipPlanMapper.toModel(membershipPlan);
    }


    @Override
    public MembershipPlanModel getMembershipPlanById(UUID id) {
        MembershipPlan membershipPlan = membershipPlanRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Plan not found"));
        return membershipPlanMapper.toModel(membershipPlan);
    }

    @Override
    public List<MembershipPlanModel> getAllMembershipPlans() {
        return membershipPlanRepository.findAll().stream()
                .map(membershipPlanMapper::toModel)
                .collect(Collectors.toList());
    }

    @Override
    public MembershipPlanModel updateMembershipPlan(UUID id, MembershipPlanModel membershipPlanModel) {
        MembershipPlan existingPlan = membershipPlanRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Plan not found"));

        existingPlan.setName(membershipPlanModel.getName());
        existingPlan.setDurationMonths(membershipPlanModel.getDurationMonths());
        existingPlan.setCost(membershipPlanModel.getCost());
        existingPlan.setDescription(membershipPlanModel.getDescription());

        existingPlan = membershipPlanRepository.save(existingPlan);
        return membershipPlanMapper.toModel(existingPlan);
    }

    @Override
    public void deleteMembershipPlan(UUID id) {
        if (!membershipPlanRepository.existsById(id)) {
            throw new ResourceNotFoundException("Plan not found");
        }
        membershipPlanRepository.deleteById(id);
    }
}
