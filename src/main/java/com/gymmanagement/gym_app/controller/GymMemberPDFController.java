package com.gymmanagement.gym_app.controller;

import com.gymmanagement.gym_app.domain.GymMember;
import com.gymmanagement.gym_app.domain.MembershipPlan;
import com.gymmanagement.gym_app.domain.Promotion;
import com.gymmanagement.gym_app.model.GymMemberModel;
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
        GymMemberModel memberModel = gymMemberService.getMemberById(id);

        if (memberModel == null) {
            return ResponseEntity.notFound().build();
        }

        // Convertimos GymMemberModel a GymMember
        GymMember gymMember = convertToEntity(memberModel);

        ByteArrayInputStream pdfStream = pdfGeneratorService.generateMemberReport(gymMember);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "inline; filename=member_report.pdf");

        return ResponseEntity.ok()
                .headers(headers)
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdfStream.readAllBytes());
    }

    private GymMember convertToEntity(GymMemberModel model) {
        GymMember entity = new GymMember();
        entity.setId(model.getId());
        entity.setName(model.getName());
        entity.setEmail(model.getEmail());
        entity.setPhone(model.getPhone());
        entity.setActive(model.getActive());
        entity.setRegistrationDate(model.getRegistrationDate());

        // Convertimos MembershipPlanModel a MembershipPlan
        if (model.getMembershipPlan() != null) {
            entity.setMembershipPlan(convertMembershipPlan(model.getMembershipPlan()));
        }

        // Convertimos lista de PromotionModel a lista de Promotion
        if (model.getPromotions() != null) {
            entity.setPromotions(model.getPromotions().stream()
                    .map(this::convertPromotion)
                    .collect(Collectors.toList()));
        }

        return entity;
    }

    private MembershipPlan convertMembershipPlan(MembershipPlanModel model) {
        MembershipPlan plan = new MembershipPlan();
        plan.setId(model.getId());
        plan.setName(model.getName());
        plan.setCost(model.getCost());
        return plan;
    }

    private Promotion convertPromotion(PromotionModel model) {
        Promotion promotion = new Promotion();
        promotion.setId(model.getId());
        promotion.setName(model.getName());
        promotion.setDiscountPercentage(model.getDiscountPercentage());
        return promotion;
    }
}
