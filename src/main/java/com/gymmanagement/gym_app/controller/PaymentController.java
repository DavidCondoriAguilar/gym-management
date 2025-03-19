package com.gymmanagement.gym_app.controller;

import com.gymmanagement.gym_app.model.PaymentModel;
import com.gymmanagement.gym_app.service.PaymentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping
    public ResponseEntity<PaymentModel> createPayment(@Valid @RequestBody PaymentModel paymentModel) {
        PaymentModel createdPayment = paymentService.createPayment(paymentModel);
        return ResponseEntity.ok(createdPayment);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PaymentModel> getPaymentById(@PathVariable UUID id) {
        PaymentModel payment = paymentService.getPaymentById(id);
        return ResponseEntity.ok(payment);
    }

    @GetMapping
    public ResponseEntity<List<PaymentModel>> getAllPayments() {
        List<PaymentModel> payments = paymentService.getAllPayments();
        return ResponseEntity.ok(payments);
    }

    @PutMapping("/{id}")
    public ResponseEntity<PaymentModel> updatePayment(@PathVariable UUID id, @Valid @RequestBody PaymentModel paymentModel) {
        PaymentModel updatedPayment = paymentService.updatePayment(id, paymentModel);
        return ResponseEntity.ok(updatedPayment);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePayment(@PathVariable UUID id) {
        paymentService.deletePayment(id);
        return ResponseEntity.noContent().build();
    }
}
