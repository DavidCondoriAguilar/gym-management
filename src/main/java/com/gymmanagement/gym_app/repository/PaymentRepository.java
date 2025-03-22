package com.gymmanagement.gym_app.repository;

import com.gymmanagement.gym_app.domain.GymMember;
import com.gymmanagement.gym_app.domain.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, UUID> {
    List<Payment> findByGymMember(GymMember gymMember);

//    @Query("SELECT SUM(p.amount) FROM Payment p WHERE p.status = 'COMPLETADO'")
//    BigDecimal getTotalRevenue();
//
//    @Query("SELECT SUM(p.amount) FROM Payment p WHERE p.status = 'PENDIENTE'")
//    BigDecimal getPendingPayments();
}
