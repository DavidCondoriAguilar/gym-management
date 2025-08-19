package com.gymmanagement.gym_app.service;

import com.gymmanagement.gym_app.dto.request.PaymentRequestDTO;
import com.gymmanagement.gym_app.dto.response.PaymentResponseDTO;
import java.util.List;
import java.util.UUID;

import com.gymmanagement.gym_app.dto.response.MemberPaymentStatusDTO;

public interface PaymentService {
    PaymentResponseDTO createPayment(PaymentRequestDTO requestDTO);
    PaymentResponseDTO getPaymentById(UUID id);
    List<PaymentResponseDTO> getAllPayments();
    PaymentResponseDTO updatePayment(UUID id, PaymentRequestDTO requestDTO);
    void deletePayment(UUID id);
    
    /**
     * Gets the payment status for a specific member, including total paid, total with discount, and remaining balance.
     * @param memberId The ID of the member
     * @return MemberPaymentStatusDTO containing payment status information
     */
    MemberPaymentStatusDTO getMemberPaymentStatus(UUID memberId);
}
