package com.gymmanagement.gym_app.service.serviceImpl;

import com.gymmanagement.gym_app.domain.MembershipPlan;
import com.gymmanagement.gym_app.domain.enums.MembershipType;
import com.gymmanagement.gym_app.dto.request.MembershipPlanRequestDTO;
import com.gymmanagement.gym_app.dto.response.MembershipPlanResponseDTO;
import com.gymmanagement.gym_app.exception.ResourceNotFoundException;
import com.gymmanagement.gym_app.mapper.MembershipPlanMapper;
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
    public MembershipPlanResponseDTO createPlan(MembershipPlanRequestDTO requestDTO) {
        MembershipPlan membershipPlan = membershipPlanMapper.fromRequestDTO(requestDTO);
        if (membershipPlan.getType() == null) {
            membershipPlan.setType(MembershipType.STANDARD);
        }
        membershipPlan = membershipPlanRepository.save(membershipPlan);
        return membershipPlanMapper.toResponseDTO(membershipPlan);
    }

    @Override
    public MembershipPlanResponseDTO getPlanById(UUID id) {
        MembershipPlan membershipPlan = membershipPlanRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Membership Plan not found"));
        return membershipPlanMapper.toResponseDTO(membershipPlan);
    }

    @Override
    public List<MembershipPlanResponseDTO> getAllPlans() {
        return membershipPlanRepository.findAll().stream()
                .map(membershipPlanMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public MembershipPlanResponseDTO updatePlan(UUID id, MembershipPlanRequestDTO requestDTO) {
        MembershipPlan existingPlan = membershipPlanRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Membership Plan not found"));
        existingPlan.setName(requestDTO.getName());
        existingPlan.setDurationMonths(requestDTO.getDurationMonths());
        existingPlan.setCost(requestDTO.getCost());
        existingPlan.setDescription(requestDTO.getDescription());
        existingPlan.setType(MembershipType.valueOf(requestDTO.getType()));
        existingPlan = membershipPlanRepository.save(existingPlan);
        return membershipPlanMapper.toResponseDTO(existingPlan);
    }

    @Override
    public void deletePlan(UUID id) {
        if (!membershipPlanRepository.existsById(id)) {
            throw new ResourceNotFoundException("Plan not found");
        }
        membershipPlanRepository.deleteById(id);
    }
}
