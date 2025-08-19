package com.gymmanagement.gym_app.service;

import com.gymmanagement.gym_app.domain.*;
import com.gymmanagement.gym_app.domain.enums.MembershipStatus;
import com.gymmanagement.gym_app.domain.enums.MembershipType;
import com.gymmanagement.gym_app.domain.enums.PaymentMethod;
import com.gymmanagement.gym_app.domain.enums.PaymentStatus;
import com.gymmanagement.gym_app.dto.request.PaymentRequestDTO;
import com.gymmanagement.gym_app.dto.response.MemberPaymentStatusDTO;
import com.gymmanagement.gym_app.dto.response.PaymentResponseDTO;
import com.gymmanagement.gym_app.mapper.PaymentMapper;
import com.gymmanagement.gym_app.repository.GymMemberRepository;
import com.gymmanagement.gym_app.repository.PaymentRepository;
import com.gymmanagement.gym_app.repository.PromotionRepository;
import com.gymmanagement.gym_app.service.PaymentService;
import com.gymmanagement.gym_app.service.serviceImpl.PaymentServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaymentServiceImplTest {

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private GymMemberRepository gymMemberRepository;

    @Mock
    private PromotionRepository promotionRepository;

    @Mock
    private PaymentMapper paymentMapper;

    private com.gymmanagement.gym_app.service.PaymentService paymentService;

    private UUID memberId;
    private UUID promotionId;
    private GymMember testMember;
    private MembershipPlan monthlyPlan;
    private Promotion testPromotion;

    @BeforeEach
    void setUp() {
        // Initialize the service with the mocked dependencies
        paymentService = new PaymentServiceImpl(
                paymentRepository,
                gymMemberRepository,
                promotionRepository,
                paymentMapper
        );
        memberId = UUID.randomUUID();
        promotionId = UUID.randomUUID();

        // Setup test membership plan
        monthlyPlan = MembershipPlan.builder()
                .id(UUID.randomUUID())
                .name("Plan Mensual")
                .description("Acceso por 1 mes")
                .cost(new BigDecimal("150.00"))
                .durationMonths(1)
                .type(MembershipType.STANDARD)
                .build();

        // Setup test promotion
        testPromotion = Promotion.builder()
                .id(promotionId)
                .name("Descuento de Verano")
                .description("Promoción especial de verano")
                .discountPercentage(new BigDecimal("30.00"))
                .startDate(LocalDate.now().minusDays(10))
                .endDate(LocalDate.now().plusDays(20))
                .maxUses(100)
                .usageCount(0)
                .build();

        // Setup active membership record first
        MembershipRecord activeMembership = MembershipRecord.builder()
                .id(UUID.randomUUID())
                .membershipPlan(monthlyPlan)
                .startDate(LocalDate.now().minusDays(5))
                .endDate(LocalDate.now().plusDays(25))
                .status(MembershipStatus.ACTIVE)
                .active(true)
                .notes("Membresía de prueba")
                .build();

        // Setup test member with all required fields
        testMember = GymMember.builder()
                .id(memberId)
                .name("Juan Pérez")
                .email("juan@example.com")
                .phone("123456789")
                .active(true)
                .registrationDate(LocalDate.now().minusDays(10))
                .membershipStartDate(LocalDate.now().minusDays(5))
                .membershipEndDate(LocalDate.now().plusDays(25))
                .membershipPlan(monthlyPlan)
                .membershipRecords(Collections.singletonList(activeMembership))
                .build();

        // Set up the bidirectional relationship
        if (activeMembership != null) {
            activeMembership.setGymMember(testMember);
        }
    }

    @Test
    void getMemberPaymentStatus_WithActivePromotion_ShouldCalculateDiscount() {
        // Arrange
        testMember.setPromotions(Collections.singletonList(testPromotion));

        when(gymMemberRepository.findById(memberId)).thenReturn(Optional.of(testMember));
        when(gymMemberRepository.findByIdWithMemberships(memberId)).thenReturn(Optional.of(testMember));
        when(gymMemberRepository.findByIdWithPromotions(memberId)).thenReturn(Optional.of(testMember));
        when(gymMemberRepository.findByIdWithPayments(memberId)).thenReturn(Optional.of(testMember));

        // Act
        MemberPaymentStatusDTO result = paymentService.getMemberPaymentStatus(memberId);

        // Assert
        assertNotNull(result);
        assertEquals(memberId, result.getMemberId());
        assertEquals("Juan Pérez", result.getMemberName());
        assertEquals(0, new BigDecimal("0.00").compareTo(result.getTotalPaid()));
        assertEquals(0, new BigDecimal("105.00").compareTo(result.getTotalWithDiscount()));
        assertEquals(0, new BigDecimal("105.00").compareTo(result.getRemainingBalance())); // 105 - 0 (no payments)
        assertFalse(result.isFullyPaid());
        assertEquals(promotionId, result.getActivePromotionId());
        assertEquals("Descuento de Verano", result.getActivePromotionName());
        assertEquals(0, new BigDecimal("30.00").compareTo(result.getDiscountPercentage()));
    }

    @Test
    void getMemberPaymentStatus_WithPartialPayment_ShouldCalculateRemainingBalance() {
        // Arrange
        testMember.setPromotions(Collections.singletonList(testPromotion));

        // Create a completed payment of $50
        Payment payment = Payment.builder()
                .amount(new BigDecimal("50.00"))
                .status(PaymentStatus.COMPLETADO)
                .paymentDate(LocalDate.now())
                .build();
        testMember.setPayments(Collections.singletonList(payment));

        when(gymMemberRepository.findById(memberId)).thenReturn(Optional.of(testMember));
        when(gymMemberRepository.findByIdWithMemberships(memberId)).thenReturn(Optional.of(testMember));
        when(gymMemberRepository.findByIdWithPromotions(memberId)).thenReturn(Optional.of(testMember));
        when(gymMemberRepository.findByIdWithPayments(memberId)).thenReturn(Optional.of(testMember));

        // Act
        MemberPaymentStatusDTO result = paymentService.getMemberPaymentStatus(memberId);

        // Assert
        assertNotNull(result);
        assertEquals(0, new BigDecimal("50.00").compareTo(result.getTotalPaid()));
        assertEquals(0, new BigDecimal("105.00").compareTo(result.getTotalWithDiscount()));
        assertEquals(0, new BigDecimal("55.00").compareTo(result.getRemainingBalance())); // 105 - 50
        assertFalse(result.isFullyPaid());
    }

    @Test
    void getMemberPaymentStatus_WithFullPayment_ShouldShowFullyPaid() {
        // Arrange
        testMember.setPromotions(Collections.singletonList(testPromotion));

        // Create a completed payment that covers the full amount
        Payment payment = Payment.builder()
                .amount(new BigDecimal("105.00")) // 150 - 30%
                .status(PaymentStatus.COMPLETADO)
                .paymentDate(LocalDate.now())
                .build();
        testMember.setPayments(Collections.singletonList(payment));

        when(gymMemberRepository.findById(memberId)).thenReturn(Optional.of(testMember));
        when(gymMemberRepository.findByIdWithMemberships(memberId)).thenReturn(Optional.of(testMember));
        when(gymMemberRepository.findByIdWithPromotions(memberId)).thenReturn(Optional.of(testMember));
        when(gymMemberRepository.findByIdWithPayments(memberId)).thenReturn(Optional.of(testMember));

        // Act
        MemberPaymentStatusDTO result = paymentService.getMemberPaymentStatus(memberId);

        // Assert
        assertNotNull(result);
        assertEquals(0, new BigDecimal("105.00").compareTo(result.getTotalPaid()));
        assertEquals(0, new BigDecimal("105.00").compareTo(result.getTotalWithDiscount()));
        assertEquals(0, BigDecimal.ZERO.compareTo(result.getRemainingBalance()));
        assertTrue(result.isFullyPaid());
    }

    @Test
    void getMemberPaymentStatus_WithoutPromotion_ShouldUseFullPrice() {
        // Arrange - No promotions
        testMember.setPromotions(Collections.emptyList());

        when(gymMemberRepository.findById(memberId)).thenReturn(Optional.of(testMember));
        when(gymMemberRepository.findByIdWithMemberships(memberId)).thenReturn(Optional.of(testMember));
        when(gymMemberRepository.findByIdWithPromotions(memberId)).thenReturn(Optional.of(testMember));
        when(gymMemberRepository.findByIdWithPayments(memberId)).thenReturn(Optional.of(testMember));

        // Act
        MemberPaymentStatusDTO result = paymentService.getMemberPaymentStatus(memberId);

        // Assert
        assertNotNull(result);
        assertEquals(0, new BigDecimal("150.00").compareTo(result.getTotalWithDiscount())); // No discount
        assertNull(result.getActivePromotionId());
        assertNull(result.getActivePromotionName());
        assertEquals(0, BigDecimal.ZERO.compareTo(result.getDiscountPercentage()));
    }

    @Test
    void getMemberPaymentStatus_WithExpiredPromotion_ShouldNotApplyDiscount() {
        // Arrange - Expired promotion
        testPromotion.setEndDate(LocalDate.now().minusDays(1));
        testMember.setPromotions(Collections.singletonList(testPromotion));

        when(gymMemberRepository.findById(memberId)).thenReturn(Optional.of(testMember));
        when(gymMemberRepository.findByIdWithMemberships(memberId)).thenReturn(Optional.of(testMember));
        when(gymMemberRepository.findByIdWithPromotions(memberId)).thenReturn(Optional.of(testMember));
        when(gymMemberRepository.findByIdWithPayments(memberId)).thenReturn(Optional.of(testMember));

        // Act
        MemberPaymentStatusDTO result = paymentService.getMemberPaymentStatus(memberId);

        // Assert
        assertNotNull(result);
        assertEquals(0, new BigDecimal("150.00").compareTo(result.getTotalWithDiscount())); // No discount applied
        assertNull(result.getActivePromotionId()); // No active promotion
    }

    @Test
    void createPayment_WithDuplicatePayment_ShouldThrowException() {
        // Arrange
        BigDecimal amount = new BigDecimal("100.00");
        LocalDate paymentDate = LocalDate.now();

        // Create a payment request with all required fields
        PaymentRequestDTO requestDTO = new PaymentRequestDTO();
        requestDTO.setMemberId(memberId);
        requestDTO.setAmount(amount);
        requestDTO.setPaymentDate(paymentDate);
        requestDTO.setStatus(PaymentStatus.COMPLETADO);
        requestDTO.setMethod(PaymentMethod.EFECTIVO);

        // Mock member exists
        when(gymMemberRepository.findById(memberId)).thenReturn(Optional.of(testMember));

        // Mock the duplicate check to return true
        when(paymentRepository.existsByGymMemberIdAndAmountAndPaymentDate(memberId, amount, paymentDate))
                .thenReturn(true);

        // Act & Assert
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            paymentService.createPayment(requestDTO);
        });

        assertEquals("Ya existe un pago idéntico para este miembro en la misma fecha", exception.getMessage());
    }

    @Test
    void createPayment_WithNewPayment_ShouldCreateSuccessfully() {
        // Arrange
        BigDecimal amount = new BigDecimal("100.00");
        LocalDate paymentDate = LocalDate.now();

        // Create a payment request with all required fields
        PaymentRequestDTO requestDTO = new PaymentRequestDTO();
        requestDTO.setMemberId(memberId);
        requestDTO.setAmount(amount);
        requestDTO.setPaymentDate(paymentDate);
        requestDTO.setStatus(PaymentStatus.COMPLETADO);
        requestDTO.setMethod(PaymentMethod.EFECTIVO);

        // Mock member exists
        when(gymMemberRepository.findById(memberId)).thenReturn(Optional.of(testMember));

        // Mock the duplicate check to return false (no duplicate)
        when(paymentRepository.existsByGymMemberIdAndAmountAndPaymentDate(memberId, amount, paymentDate))
                .thenReturn(false);

        // Mock payment mapper
        Payment payment = new Payment();
        when(paymentMapper.fromRequestDTO(any(PaymentRequestDTO.class), any(), any()))
                .thenReturn(payment);

        PaymentResponseDTO responseDTO = new PaymentResponseDTO();
        when(paymentMapper.toResponseDTO(any(Payment.class))).thenReturn(responseDTO);

        // Mock payment save
        when(paymentRepository.save(any(Payment.class))).thenReturn(payment);

        // Act
        PaymentResponseDTO result = paymentService.createPayment(requestDTO);

        // Assert
        assertNotNull(result);
        verify(paymentRepository, times(1)).save(any(Payment.class));
    }

    @Test
    void createPayment_WithValidData_ReturnsPaymentResponse() {
        // Arrange
        PaymentRequestDTO requestDTO = new PaymentRequestDTO();
        requestDTO.setMemberId(memberId);
        requestDTO.setAmount(new BigDecimal("50.00")); // Match the membership plan cost
        requestDTO.setPaymentDate(LocalDate.now());
        requestDTO.setMethod(PaymentMethod.EFECTIVO);
        requestDTO.setStatus(PaymentStatus.COMPLETADO);

        Payment payment = new Payment();
        payment.setId(UUID.randomUUID());
        payment.setAmount(requestDTO.getAmount());
        payment.setPaymentDate(requestDTO.getPaymentDate());
        payment.setPaymentMethod(requestDTO.getMethod());
        payment.setStatus(requestDTO.getStatus());

        when(gymMemberRepository.findByIdWithMembershipPlan(memberId)).thenReturn(Optional.of(testMember));
        when(paymentRepository.existsByGymMemberIdAndAmountAndPaymentDate(any(), any(), any())).thenReturn(false);
        when(paymentMapper.fromRequestDTO(any(), any(), any())).thenReturn(payment);
        when(paymentRepository.save(any(Payment.class))).thenReturn(payment);
        when(paymentMapper.toResponseDTO(any(Payment.class))).thenReturn(new PaymentResponseDTO());

        // Act
        PaymentResponseDTO response = paymentService.createPayment(requestDTO);

        // Assert
        assertNotNull(response);
        verify(paymentRepository).save(any(Payment.class));
    }
    
    @Test
    void createPayment_WithIncorrectAmount_ThrowsException() {
        // Arrange
        PaymentRequestDTO requestDTO = new PaymentRequestDTO();
        requestDTO.setMemberId(memberId);
        requestDTO.setAmount(new BigDecimal("45.00")); // Incorrect amount (should be 50.00)
        requestDTO.setPaymentDate(LocalDate.now());
        requestDTO.setMethod(PaymentMethod.EFECTIVO);
        requestDTO.setStatus(PaymentStatus.COMPLETADO);

        when(gymMemberRepository.findByIdWithMembershipPlan(memberId)).thenReturn(Optional.of(testMember));
        when(paymentRepository.existsByGymMemberIdAndAmountAndPaymentDate(any(), any(), any())).thenReturn(false);

        // Act & Assert
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            paymentService.createPayment(requestDTO);
        });

        assertTrue(exception.getMessage().contains("no coincide con el monto requerido"));
        verify(paymentRepository, never()).save(any(Payment.class));
    }
    
    @Test
    void createPayment_WithPromotionAndCorrectAmount_ReturnsPaymentResponse() {
        // Arrange
        UUID promotionId = UUID.randomUUID();
        Promotion promotion = new Promotion();
        promotion.setId(promotionId);
        promotion.setName("20% de descuento");
        promotion.setDiscountPercentage(new BigDecimal("20.00")); // 20% discount
        promotion.setStartDate(LocalDate.now().minusDays(1));
        promotion.setEndDate(LocalDate.now().plusDays(30));
        
        // Membership plan costs 50.00, with 20% discount = 40.00 expected
        BigDecimal originalAmount = new BigDecimal("50.00");
        BigDecimal discountedAmount = new BigDecimal("40.00");
        
        PaymentRequestDTO requestDTO = new PaymentRequestDTO();
        requestDTO.setMemberId(memberId);
        requestDTO.setAmount(discountedAmount); // Paying the discounted amount
        requestDTO.setPaymentDate(LocalDate.now());
        requestDTO.setMethod(PaymentMethod.EFECTIVO);
        requestDTO.setStatus(PaymentStatus.COMPLETADO);
        requestDTO.setPromotionId(promotionId);
        
        // Set up test member with membership plan
        testMember.setMembershipPlan(monthlyPlan);
        monthlyPlan.setCost(originalAmount);
        
        when(gymMemberRepository.findByIdWithMembershipPlan(memberId)).thenReturn(Optional.of(testMember));
        when(promotionRepository.findById(promotionId)).thenReturn(Optional.of(promotion));
        when(paymentRepository.existsByGymMemberIdAndAmountAndPaymentDate(any(), any(), any())).thenReturn(false);
        when(paymentMapper.fromRequestDTO(any(), any(), any())).thenReturn(new Payment());
        when(paymentRepository.save(any(Payment.class))).thenAnswer(invocation -> {
            Payment savedPayment = invocation.getArgument(0);
            savedPayment.setId(UUID.randomUUID());
            return savedPayment;
        });
        when(paymentMapper.toResponseDTO(any(Payment.class))).thenReturn(new PaymentResponseDTO());

        // Act
        PaymentResponseDTO response = paymentService.createPayment(requestDTO);

        // Assert
        assertNotNull(response);
        verify(paymentRepository).save(argThat(payment -> 
            payment.getAmount().compareTo(originalAmount) == 0 && // Original amount (50.00)
            payment.getDiscountedAmount().compareTo(discountedAmount) == 0 && // After 20% discount (40.00)
            payment.getPromotion() != null &&
            payment.getPromotion().getId().equals(promotionId)
        ));
    }
    
    @Test
    void createPayment_WithPromotionAndIncorrectAmount_ThrowsExceptionWithDetailedMessage() {
        // Arrange
        UUID promotionId = UUID.randomUUID();
        Promotion promotion = new Promotion();
        promotion.setId(promotionId);
        promotion.setName("30% de descuento");
        promotion.setDiscountPercentage(new BigDecimal("30.00"));
        promotion.setStartDate(LocalDate.now().minusDays(1));
        promotion.setEndDate(LocalDate.now().plusDays(30));
        
        // Membership plan costs 100.00, with 30% discount = 70.00 expected
        BigDecimal originalAmount = new BigDecimal("100.00");
        BigDecimal discountedAmount = new BigDecimal("70.00");
        BigDecimal incorrectAmount = new BigDecimal("80.00"); // Incorrect amount
        
        PaymentRequestDTO requestDTO = new PaymentRequestDTO();
        requestDTO.setMemberId(memberId);
        requestDTO.setAmount(incorrectAmount);
        requestDTO.setPromotionId(promotionId);
        requestDTO.setPaymentDate(LocalDate.now());
        requestDTO.setMethod(PaymentMethod.TARJETA_CREDITO);
        requestDTO.setStatus(PaymentStatus.COMPLETADO);

        when(gymMemberRepository.findByIdWithMembershipPlan(memberId)).thenReturn(Optional.of(testMember));
        when(promotionRepository.findById(promotionId)).thenReturn(Optional.of(promotion));
        when(paymentRepository.existsByGymMemberIdAndAmountAndPaymentDate(any(), any(), any())).thenReturn(false);

        // Act & Assert
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            paymentService.createPayment(requestDTO);
        });

        assertTrue(exception.getMessage().contains("no coincide con el monto requerido"));
        verify(paymentRepository, never()).save(any(Payment.class));
    }
}
