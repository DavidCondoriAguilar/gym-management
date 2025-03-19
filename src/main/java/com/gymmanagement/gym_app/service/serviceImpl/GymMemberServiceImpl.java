package com.gymmanagement.gym_app.service.serviceImpl;

import com.gymmanagement.gym_app.domain.GymMember;
import com.gymmanagement.gym_app.mapper.GymMemberMapper;
import com.gymmanagement.gym_app.model.GymMemberModel;
import com.gymmanagement.gym_app.repository.GymMemberRepository;
import com.gymmanagement.gym_app.service.GymMemberService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class GymMemberServiceImpl implements GymMemberService {

    private final GymMemberRepository gymMemberRepository;
    private final GymMemberMapper gymMemberMapper;

    @Override
    public List<GymMemberModel> getAllMembers() {
        return gymMemberRepository.findAll().stream()
                .map(gymMemberMapper::toModel)
                .toList();
    }

    @Override
    public GymMemberModel getMemberById(UUID id) {
        GymMember gymMember = findMemberById(id);
        return gymMemberMapper.toModel(gymMember);
    }

    @Override
    public GymMemberModel createMember(@Valid GymMemberModel gymMemberModel) {

        if (gymMemberModel.getMembershipEnd() == null) {
            gymMemberModel.setMembershipEnd(LocalDate.now().plusMonths(1));
        }
        GymMember gymMember = gymMemberMapper.toEntity(gymMemberModel);
        gymMember = gymMemberRepository.save(gymMember);
        return gymMemberMapper.toModel(gymMember);
    }

    @Override
    public GymMemberModel updateMember(UUID id, @Valid GymMemberModel gymMemberModel) {
        GymMember existingMember = findMemberById(id);

        existingMember.setName(gymMemberModel.getName());
        existingMember.setEmail(gymMemberModel.getEmail());
        existingMember.setPhone(gymMemberModel.getPhone());
        existingMember.setActive(gymMemberModel.getActive());
        existingMember.setRegistrationDate(gymMemberModel.getRegistrationDate());
        existingMember.setMembershipStartDate(gymMemberModel.getMembershipStart());
        existingMember.setMembershipEndDate(gymMemberModel.getMembershipEnd());

        existingMember = gymMemberRepository.save(existingMember);
        return gymMemberMapper.toModel(existingMember);
    }

    @Override
    public void deleteMember(UUID id) {
        GymMember gymMember = findMemberById(id);
        gymMemberRepository.delete(gymMember);
    }

    private GymMember findMemberById(UUID id) {
        return gymMemberRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Member not found with ID: " + id));
    }
}
