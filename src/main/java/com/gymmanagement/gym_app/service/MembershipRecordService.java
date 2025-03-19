package com.gymmanagement.gym_app.service;

import com.gymmanagement.gym_app.model.MembershipRecordModel;
import java.util.List;
import java.util.UUID;

public interface MembershipRecordService {

    MembershipRecordModel createMembershipRecord(MembershipRecordModel membershipRecordModel);

    List<MembershipRecordModel> getMembershipRecordsByMember(UUID gymMemberId);

    List<MembershipRecordModel> getAllMembershipRecords();

    void deleteMembershipRecord(UUID id);

    void cancelarMembresia(UUID membershipRecordId);

}
