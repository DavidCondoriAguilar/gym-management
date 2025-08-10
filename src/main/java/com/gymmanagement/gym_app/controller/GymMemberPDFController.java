package com.gymmanagement.gym_app.controller;

import com.gymmanagement.gym_app.domain.GymMember;
import com.gymmanagement.gym_app.domain.MembershipPlan;
import com.gymmanagement.gym_app.domain.Promotion;
import com.gymmanagement.gym_app.dto.response.GymMemberResponseDTO;
import com.gymmanagement.gym_app.dto.response.PromotionSummaryDTO;
import com.gymmanagement.gym_app.dto.response.MembershipPlanSummaryDTO;
import com.gymmanagement.gym_app.model.MembershipPlanModel;
import com.gymmanagement.gym_app.model.PromotionModel;
import com.gymmanagement.gym_app.service.GymMemberService;
import com.gymmanagement.gym_app.utils.PDFGeneratorService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.ByteArrayInputStream;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("gym-members-pdf")
@RequiredArgsConstructor
public class GymMemberPDFController {

    private final PDFGeneratorService pdfGeneratorService;
    private final GymMemberService gymMemberService;

    @GetMapping("/members/{id}/report")
    public ResponseEntity<byte[]> generateMemberPdf(@PathVariable UUID id) {
        GymMemberResponseDTO memberDTO = gymMemberService.getMemberById(id);

        if (memberDTO == null) {
            return ResponseEntity.notFound().build();
        }

        // Convertimos GymMemberResponseDTO a GymMember
        GymMember gymMember = convertToEntity(memberDTO);

        ByteArrayInputStream pdfStream = pdfGeneratorService.generateMemberReport(gymMember);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "inline; filename=member_report.pdf");

        return ResponseEntity.ok()
                .headers(headers)
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdfStream.readAllBytes());
    }

    private GymMember convertToEntity(GymMemberResponseDTO dto) {
        GymMember entity = new GymMember();
        entity.setId(dto.getId());
        entity.setName(dto.getName());
        entity.setEmail(dto.getEmail());
        entity.setPhone(dto.getPhone());
        entity.setActive(dto.getActive());
        entity.setRegistrationDate(dto.getRegistrationDate());

        // Convertimos MembershipPlanModel a MembershipPlan
        if (dto.getMembershipPlan() != null) {
            entity.setMembershipPlan(convertMembershipPlan(toModel(dto.getMembershipPlan())));
        }

        // Convertimos lista de PromotionModel a lista de Promotion
        if (dto.getPromotions() != null) {
            entity.setPromotions(dto.getPromotions().stream()
                    .map(this::convertPromotion)
                    .collect(Collectors.toList()));
        }

        return entity;
    }

    private MembershipPlanModel toModel(MembershipPlanSummaryDTO dto) {
        if (dto == null) return null;
        MembershipPlanModel model = new MembershipPlanModel();
        model.setId(dto.getId());
        model.setName(dto.getName());
        model.setDurationMonths(dto.getDurationMonths());
        model.setCost(dto.getCost());
        // description and type are not present in DTO, so leave as null
        return model;
    }

    private MembershipPlan convertMembershipPlan(MembershipPlanModel model) {
        MembershipPlan plan = new MembershipPlan();
        plan.setId(model.getId());
        plan.setName(model.getName());
        plan.setCost(model.getCost());
        return plan;
    }

    private Promotion convertPromotion(PromotionSummaryDTO dto) {
        Promotion promotion = new Promotion();
        promotion.setId(dto.getId());
        promotion.setName(dto.getName());
        promotion.setDiscountPercentage(dto.getDiscountPercentage());
        return promotion;
    }
}
