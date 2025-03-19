package com.gymmanagement.gym_app.validation;

import com.gymmanagement.gym_app.domain.GymMember;
import com.gymmanagement.gym_app.domain.Payment;
import jakarta.validation.ValidationException;

import java.math.BigDecimal;
import java.util.List;

public class PaymentValidator {

    public static void validatePayment(GymMember gymMember, BigDecimal newPaymentAmount, List<Payment> existingPayments) {
        // Obtener el costo total de la membresía
        BigDecimal membershipCost = gymMember.getMembershipPlan().getCost();

        // Calcular el total de pagos realizados
        BigDecimal totalPaid = existingPayments.stream()
                .map(p -> new BigDecimal(String.valueOf(p.getAmount())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Calcular el nuevo total con el pago actual
        BigDecimal newTotal = totalPaid.add(newPaymentAmount);

        // Validar que no se supere el costo de la membresía
        if (newTotal.compareTo(membershipCost) > 0) {
            throw new ValidationException("El total de pagos excede el costo de la membresía.");
        }
    }
}
