package com.gymmanagement.gym_app.controller;

import com.gymmanagement.gym_app.dto.request.PaymentRequestDTO;
import com.gymmanagement.gym_app.dto.response.PaymentResponseDTO;
import com.gymmanagement.gym_app.service.PaymentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * REST controller for managing payments in the gym management system.
 * All endpoints are versioned under /api/v1/
 */
@RestController
@RequestMapping("/payments")
@RequiredArgsConstructor
@Tag(name = "Payments", description = "API for managing payment operations")
public class PaymentController {

    private final PaymentService paymentService;

    @PreAuthorize("hasRole('ADMIN')")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(
        summary = "Create a new payment",
        description = "Creates a new payment record in the system"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Payment created successfully",
            content = @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = PaymentResponseDTO.class),
                examples = @ExampleObject(
                    value = "{\n                        \"id\": \"550e8400-e29b-41d4-a716-446655440000\",\n                        \"amount\": 100.00,\n                        \"paymentDate\": \"2025-06-11\",\n                        \"paymentMethod\": \"TARJETA_CREDITO\",\n                        \"status\": \"COMPLETADO\"\n                    }"
                )
            )
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid input data",
            content = @Content
        ),
        @ApiResponse(
            responseCode = "500",
            description = "Internal server error",
            content = @Content
        )
    })
    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<PaymentResponseDTO> createPayment(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                description = "Payment details to be created",
                required = true,
                content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = PaymentRequestDTO.class),
                    examples = @ExampleObject(
                        value = "{\n                            \"amount\": 100.00,\n                            \"paymentDate\": \"2025-06-11\",\n                            \"paymentMethod\": \"TARJETA_CREDITO\",\n                            \"status\": \"PENDIENTE\",\n                            \"gymMemberId\": \"32e34ad4-d55a-4211-9fe0-bfcb24ed008e\"\n                        }"
                    )
                )
            )
            @Valid @RequestBody PaymentRequestDTO requestDTO) {
        return ResponseEntity.ok(paymentService.createPayment(requestDTO));
    }

    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    @Operation(
        summary = "Get payment by ID",
        description = "Retrieves payment details by its unique identifier"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Payment found",
            content = @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = PaymentResponseDTO.class)
            )
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Payment not found",
            content = @Content
        )
    })
    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<PaymentResponseDTO> getPaymentById(
            @Parameter(
                description = "ID of the payment to be retrieved",
                required = true,
                schema = @Schema(type = "string")
            )
            @PathVariable UUID id) {
        return ResponseEntity.ok(paymentService.getPaymentById(id));
    }

    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<PaymentResponseDTO>> getAllPayments() {
        return ResponseEntity.ok(paymentService.getAllPayments());
    }

    @PreAuthorize("hasRole('ADMIN')")
    @SecurityRequirement(name = "bearerAuth")
    @PutMapping("/{id}")
    public ResponseEntity<PaymentResponseDTO> updatePayment(@PathVariable UUID id, @Valid @RequestBody PaymentRequestDTO requestDTO) {
        return ResponseEntity.ok(paymentService.updatePayment(id, requestDTO));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @SecurityRequirement(name = "bearerAuth")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePayment(@PathVariable UUID id) {
        paymentService.deletePayment(id);
        return ResponseEntity.noContent().build();
    }
}
