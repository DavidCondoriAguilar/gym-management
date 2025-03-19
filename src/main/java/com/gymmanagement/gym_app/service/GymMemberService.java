package com.gymmanagement.gym_app.service;

import com.gymmanagement.gym_app.model.GymMemberModel;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public interface GymMemberService {
    List<GymMemberModel> getAllMembers();
    GymMemberModel getMemberById(UUID id);
    GymMemberModel createMember(GymMemberModel gymMemberModel);
    GymMemberModel updateMember(UUID id, GymMemberModel gymMemberModel);
    void deleteMember(UUID id);
}
