package com.gymmanagement.gym_app.service.serviceImpl;

import com.gymmanagement.gym_app.domain.GymMember;
import com.gymmanagement.gym_app.domain.MembershipPlan;
import com.gymmanagement.gym_app.domain.MembershipRecord;
import com.gymmanagement.gym_app.exception.ResourceNotFoundException;
import com.gymmanagement.gym_app.mapper.MembershipRecordMapper;
import com.gymmanagement.gym_app.model.MembershipRecordModel;
import com.gymmanagement.gym_app.repository.GymMemberRepository;
import com.gymmanagement.gym_app.repository.MembershipPlanRepository;
import com.gymmanagement.gym_app.repository.MembershipRecordRepository;
import com.gymmanagement.gym_app.service.MembershipRecordService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class MembershipRecordServiceImpl implements MembershipRecordService {

    private final MembershipRecordRepository membershipRecordRepository;
    private final GymMemberRepository gymMemberRepository;
    private final MembershipPlanRepository membershipPlanRepository;
    private final MembershipRecordMapper membershipRecordMapper;

    @Override
    public MembershipRecordModel createMembershipRecord(MembershipRecordModel membershipRecordModel) {
        // Buscar el GymMember por ID
        GymMember gymMember = gymMemberRepository.findById(membershipRecordModel.getGymMemberId())
                .orElseThrow(() -> new RuntimeException("GymMember no encontrado"));

        // Buscar el MembershipPlan por ID
        MembershipPlan membershipPlan = membershipPlanRepository.findById(membershipRecordModel.getMembershipPlanId())
                .orElseThrow(() -> new RuntimeException("MembershipPlan no encontrado"));

        // Crear la entidad MembershipRecord
        MembershipRecord record = MembershipRecord.builder()
                .gymMember(gymMember)
                .membershipPlan(membershipPlan)
                .startDate(membershipRecordModel.getStartDate())
                .endDate(membershipRecordModel.getEndDate())
                .active(true)
                .cancellationDate(membershipRecordModel.getCancellationDate())
                .build();

        MembershipRecord savedRecord = membershipRecordRepository.save(record);
        return membershipRecordMapper.toModel(savedRecord);
    }

    @Override
    public List<MembershipRecordModel> getMembershipRecordsByMember(UUID gymMemberId) {
        GymMember gymMember = gymMemberRepository.findById(gymMemberId)
                .orElseThrow(() -> new RuntimeException("GymMember no encontrado"));

        return membershipRecordRepository.findByGymMember(gymMember)
                .stream()
                .map(membershipRecordMapper::toModel)
                .collect(Collectors.toList());
    }

    @Override
    public List<MembershipRecordModel> getAllMembershipRecords() {
        return membershipRecordRepository.findAll()
                .stream()
                .map(membershipRecordMapper::toModel)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteMembershipRecord(UUID id) {
        if (!membershipRecordRepository.existsById(id)) {
            throw new RuntimeException("MembershipRecord no encontrado");
        }
        membershipRecordRepository.deleteById(id);
    }

    @Transactional
    public void cancelarMembresia(UUID memberId) {
        MembershipRecord record = membershipRecordRepository.findByGymMember_IdAndActive(memberId, true)
                .orElseThrow(() -> new ResourceNotFoundException("Membresía activa no encontrada para el usuario: " + memberId));

        record.setActive(false);
        record.setCancellationDate(LocalDate.now());  // Guarda la fecha de cancelación
        membershipRecordRepository.save(record);
    }
}
