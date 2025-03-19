package com.gymmanagement.gym_app.repository;

import com.gymmanagement.gym_app.domain.MembershipPlan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface MembershipPlanRepository extends JpaRepository<MembershipPlan, UUID> {
    boolean existsByName(String name);

}
