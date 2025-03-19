package com.gymmanagement.gym_app.repository;

import com.gymmanagement.gym_app.domain.MembershipRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface MembershipRecordRepository extends JpaRepository<MembershipRecord, UUID> {
}
