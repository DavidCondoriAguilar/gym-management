package com.gymmanagement.gym_app.service;

import com.gymmanagement.gym_app.dto.request.GymMemberRequestDTO;
import com.gymmanagement.gym_app.dto.response.GymMemberResponseDTO;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public interface GymMemberService {
    List<GymMemberResponseDTO> getAllMembers();
    GymMemberResponseDTO getMemberById(UUID id);
    GymMemberResponseDTO createMember(GymMemberRequestDTO gymMemberRequestDTO);
    GymMemberResponseDTO updateMember(UUID id, GymMemberRequestDTO gymMemberRequestDTO);
    void deleteMember(UUID id);
}
