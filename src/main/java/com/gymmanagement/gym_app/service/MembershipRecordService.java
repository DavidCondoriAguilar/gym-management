package com.gymmanagement.gym_app.service;

import com.gymmanagement.gym_app.dto.request.MembershipRecordRequestDTO;
import com.gymmanagement.gym_app.dto.response.MembershipRecordResponseDTO;
import java.util.List;
import java.util.UUID;

public interface MembershipRecordService {

    MembershipRecordResponseDTO createRecord(MembershipRecordRequestDTO requestDTO);

    List<MembershipRecordResponseDTO> getRecordsByMember(UUID gymMemberId);

    List<MembershipRecordResponseDTO> getAllRecords();

    void deleteRecord(UUID id);

    void cancelarMembresia(UUID membershipRecordId);

}
