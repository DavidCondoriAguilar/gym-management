package com.gymmanagement.gym_app.mapper;

import com.gymmanagement.gym_app.domain.GymMember;
import com.gymmanagement.gym_app.domain.MembershipPlan;
import com.gymmanagement.gym_app.domain.Promotion;
import com.gymmanagement.gym_app.dto.response.PromotionSummaryDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class PromotionMapperTest {

    private PromotionMapper promotionMapper;
    private Promotion promotion;
    private MembershipPlan plan1;
    private MembershipPlan plan2;
    private GymMember member1;
    private GymMember member2;
    private GymMember memberWithoutPlan;

    @BeforeEach
    void setUp() {
        promotionMapper = Mappers.getMapper(PromotionMapper.class);
        
        // Create test data
        promotion = new Promotion();
        promotion.setId(UUID.randomUUID());
        promotion.setName("Test Promotion");
        promotion.setDiscountPercentage(new BigDecimal("30.00"));
        
        // Create membership plans
        plan1 = new MembershipPlan();
        plan1.setId(UUID.randomUUID());
        plan1.setName("Basic Plan");
        plan1.setCost(new BigDecimal("100.00"));
        
        plan2 = new MembershipPlan();
        plan2.setId(UUID.randomUUID());
        plan2.setName("Premium Plan");
        plan2.setCost(new BigDecimal("200.00"));
        
        // Create members with plans
        member1 = new GymMember();
        member1.setId(UUID.randomUUID());
        member1.setName("John Doe");
        member1.setMembershipPlan(plan1);
        
        member2 = new GymMember();
        member2.setId(UUID.randomUUID());
        member2.setName("Jane Smith");
        member2.setMembershipPlan(plan2);
        
        // Member without plan
        memberWithoutPlan = new GymMember();
        memberWithoutPlan.setId(UUID.randomUUID());
        memberWithoutPlan.setName("No Plan Member");
    }

    @Test
    void toSummaryDTO_WithNull_ShouldReturnNull() {
        assertNull(promotionMapper.toSummaryDTO(null));
    }

    @Test
    void toSummaryDTO_WithNoMembers_ShouldReturnEmptyValues() {
        // Given
        promotion.setGymMembers(null);
        
        // When
        PromotionSummaryDTO result = promotionMapper.toSummaryDTO(promotion);
        
        // Then
        assertNotNull(result);
        assertEquals(promotion.getId(), result.getId());
        assertEquals(promotion.getName(), result.getName());
        assertEquals(0, result.getDiscountPercentage().compareTo(new BigDecimal("30.00")), "Discount percentage should be 30.00");
        assertEquals(0, result.getTotalOriginal().compareTo(new BigDecimal("0.00")), "Total original should be 0.00");
        assertEquals(0, result.getTotalDiscount().compareTo(new BigDecimal("0.00")), "Total discount should be 0.00");
        assertEquals(0, result.getTotalWithDiscount().compareTo(new BigDecimal("0.00")), "Total with discount should be 0.00");
        assertEquals(0, result.getMemberCount(), "Member count should be 0");
    }

    @Test
    void toSummaryDTO_WithMembers_ShouldCalculateCorrectly() {
        // Given
        promotion.setGymMembers(Arrays.asList(member1, member2, memberWithoutPlan));
        
        // When
        PromotionSummaryDTO result = promotionMapper.toSummaryDTO(promotion);
        
        // Then
        assertNotNull(result);
        assertEquals(promotion.getId(), result.getId());
        assertEquals(promotion.getName(), result.getName());
        assertEquals(new BigDecimal("30.00"), result.getDiscountPercentage());
        
        // Total original: 100 + 200 = 300
        assertEquals(0, result.getTotalOriginal().compareTo(new BigDecimal("300.00")), "Total original should be 300.00");
        
        // Total discount: (100 * 0.30) + (200 * 0.30) = 30 + 60 = 90
        assertEquals(0, result.getTotalDiscount().compareTo(new BigDecimal("90.00")), "Total discount should be 90.00");
        
        // Total with discount: 300 - 90 = 210
        assertEquals(0, result.getTotalWithDiscount().compareTo(new BigDecimal("210.00")), "Total with discount should be 210.00");
        
        // 3 members total, but only 2 have plans
        assertEquals(3, result.getMemberCount(), "Should count all members including those without plans");
    }

    @Test
    void toSummaryDTO_WithMemberWithoutPlan_ShouldHandleCorrectly() {
        // Given
        promotion.setGymMembers(Arrays.asList(memberWithoutPlan));
        
        // When
        PromotionSummaryDTO result = promotionMapper.toSummaryDTO(promotion);
        
        // Then
        assertNotNull(result);
        assertEquals(0, result.getTotalOriginal().compareTo(new BigDecimal("0.00")), "Total original should be 0.00");
        assertEquals(0, result.getTotalDiscount().compareTo(new BigDecimal("0.00")), "Total discount should be 0.00");
        assertEquals(0, result.getTotalWithDiscount().compareTo(new BigDecimal("0.00")), "Total with discount should be 0.00");
        assertEquals(1, result.getMemberCount(), "Should count member even without plan");
    }

    @Test
    void toSummaryDTO_WithZeroDiscount_ShouldReturnOriginalAmount() {
        // Given
        promotion.setDiscountPercentage(BigDecimal.ZERO);
        promotion.setGymMembers(Arrays.asList(member1, member2));
        
        // When
        PromotionSummaryDTO result = promotionMapper.toSummaryDTO(promotion);
        
        // Then
        assertNotNull(result);
        assertEquals(0, result.getTotalOriginal().compareTo(new BigDecimal("300.00")), "Total original should be 300.00");
        assertEquals(0, result.getTotalDiscount().compareTo(new BigDecimal("0.00")), "Total discount should be 0.00");
        assertEquals(0, result.getTotalWithDiscount().compareTo(new BigDecimal("300.00")), "Total with discount should be 300.00");
    }
}
