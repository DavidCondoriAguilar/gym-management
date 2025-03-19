package com.gymmanagement.gym_app.repository;

import com.gymmanagement.gym_app.domain.GymMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface GymMemberRepository extends JpaRepository<GymMember, UUID> {

    Optional<GymMember> findByEmail(String email);
}
