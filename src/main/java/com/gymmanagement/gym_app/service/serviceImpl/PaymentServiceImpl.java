package com.gymmanagement.gym_app.service.serviceImpl;

import com.gymmanagement.gym_app.domain.GymMember;
import com.gymmanagement.gym_app.domain.Payment;
import com.gymmanagement.gym_app.domain.enums.PaymentStatus;
import com.gymmanagement.gym_app.exception.ResourceNotFoundException;
import com.gymmanagement.gym_app.mapper.PaymentMapper;
import com.gymmanagement.gym_app.model.PaymentModel;
import com.gymmanagement.gym_app.repository.GymMemberRepository;
import com.gymmanagement.gym_app.repository.PaymentRepository;
import com.gymmanagement.gym_app.service.PaymentService;
import com.gymmanagement.gym_app.validation.PaymentValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final GymMemberRepository gymMemberRepository;
    private final PaymentMapper paymentMapper;

    @Override
    public PaymentModel createPayment(PaymentModel paymentModel) {
        GymMember gymMember = gymMemberRepository.findById(paymentModel.getGymMemberId())
                .orElseThrow(() -> new ResourceNotFoundException("GymMember not found with ID: " + paymentModel.getGymMemberId()));

        // Obtener pagos existentes del miembro
        List<Payment> payments = paymentRepository.findByGymMember(gymMember);

        // Validar si el pago excede el costo de la membresía
        PaymentValidator.validatePayment(gymMember, new BigDecimal(String.valueOf(paymentModel.getAmount())), payments);

        // Mapear el modelo de pago a la entidad
        Payment payment = paymentMapper.toEntity(paymentModel);
        payment.setGymMember(gymMember);

        // Guardar el pago
        payment = paymentRepository.save(payment);

        // Verificar el total de pagos después de registrar el nuevo
        payments.add(payment);
        BigDecimal totalPaid = payments.stream()
                .map(p -> new BigDecimal(String.valueOf(p.getAmount())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal membershipCost = gymMember.getMembershipPlan().getCost();

        // Si el total pagado es igual o mayor al costo, marcarlo como COMPLETADO
        if (totalPaid.compareTo(membershipCost) >= 0) {
            payment.setStatus(PaymentStatus.COMPLETADO);
            paymentRepository.save(payment);
        }

        return paymentMapper.toModel(payment);
    }

    @Override
    public PaymentModel getPaymentById(UUID id) {
        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found with ID: " + id));
        return paymentMapper.toModel(payment);
    }

    @Override
    public List<PaymentModel> getAllPayments() {
        return paymentRepository.findAll().stream()
                .map(paymentMapper::toModel)
                .collect(Collectors.toList());
    }

    @Override
    public PaymentModel updatePayment(UUID id, PaymentModel paymentModel) {
        Payment existingPayment = paymentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found with ID: " + id));

        existingPayment.setAmount(paymentModel.getAmount());
        existingPayment.setPaymentDate(paymentModel.getPaymentDate());
        existingPayment.setPaymentMethod(paymentModel.getPaymentMethod());
        existingPayment.setStatus(paymentModel.getStatus());

        existingPayment = paymentRepository.save(existingPayment);
        return paymentMapper.toModel(existingPayment);
    }


    @Override
    public void deletePayment(UUID id) {
        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found with ID: " + id));
        paymentRepository.delete(payment);
    }
}
