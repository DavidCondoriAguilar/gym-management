package com.gymmanagement.gym_app.repository;

import com.gymmanagement.gym_app.domain.GymMember;
import com.gymmanagement.gym_app.domain.MembershipRecord;
import com.gymmanagement.gym_app.model.GymMemberModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface MembershipRecordRepository extends JpaRepository<MembershipRecord, UUID> {
    List<MembershipRecord> findByGymMember(GymMember gymMember);
    GymMemberModel getGymMemberById(UUID id);
    Optional<MembershipRecord> findByGymMember_IdAndActive(UUID gymMemberId, boolean active);

}
