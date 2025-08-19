package com.gymmanagement.gym_app.repository;

import com.gymmanagement.gym_app.domain.GymMember;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface GymMemberRepository extends JpaRepository<GymMember, UUID> {

    Optional<GymMember> findByEmail(String email);
    
    @EntityGraph(attributePaths = {"membershipRecords.membershipPlan"})
    @Query("SELECT gm FROM GymMember gm WHERE gm.id = :memberId")
    Optional<GymMember> findByIdWithMemberships(@Param("memberId") UUID memberId);
    
    @EntityGraph(attributePaths = {"promotions"})
    @Query("SELECT gm FROM GymMember gm WHERE gm.id = :memberId")
    Optional<GymMember> findByIdWithPromotions(@Param("memberId") UUID memberId);
    
    @EntityGraph(attributePaths = {"payments"})
    @Query("SELECT gm FROM GymMember gm WHERE gm.id = :memberId")
    Optional<GymMember> findByIdWithPayments(@Param("memberId") UUID memberId);
    
    @EntityGraph(attributePaths = {"membershipPlan"})
    @Query("SELECT gm FROM GymMember gm LEFT JOIN FETCH gm.membershipPlan WHERE gm.id = :memberId")
    Optional<GymMember> findByIdWithMembershipPlan(@Param("memberId") UUID memberId);
}
