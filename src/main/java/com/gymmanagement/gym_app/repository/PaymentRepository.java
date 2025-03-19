package com.gymmanagement.gym_app.repository;

import com.gymmanagement.gym_app.domain.GymMember;
import com.gymmanagement.gym_app.domain.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, UUID> {
    List<Payment> findByGymMember(GymMember gymMember);
}
