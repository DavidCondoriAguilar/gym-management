package com.gymmanagement.gym_app.controller;

import com.gymmanagement.gym_app.dto.request.MembershipPlanRequestDTO;
import com.gymmanagement.gym_app.dto.response.MembershipPlanResponseDTO;
import com.gymmanagement.gym_app.service.MembershipPlanService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/membership-plans")
@RequiredArgsConstructor
public class MembershipPlanController {

    private final MembershipPlanService membershipPlanService;

    @GetMapping
    @PreAuthorize("permitAll")
    public ResponseEntity<List<MembershipPlanResponseDTO>> listMembershipPlans() {
        return ResponseEntity.ok(membershipPlanService.getAllPlans());
    }

    @GetMapping("/{id}")
    @PreAuthorize("permitAll")
    public ResponseEntity<MembershipPlanResponseDTO> getMembershipPlanById(@PathVariable UUID id) {
        return ResponseEntity.ok(membershipPlanService.getPlanById(id));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @SecurityRequirement(name = "bearerAuth")
    @PostMapping
    public ResponseEntity<MembershipPlanResponseDTO> createMembershipPlan(@Valid @RequestBody MembershipPlanRequestDTO requestDTO) {
        return ResponseEntity.ok(membershipPlanService.createPlan(requestDTO));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @SecurityRequirement(name = "bearerAuth")
    @PutMapping("/{id}")
    public ResponseEntity<MembershipPlanResponseDTO> updateMembershipPlan(@PathVariable UUID id, @Valid @RequestBody MembershipPlanRequestDTO requestDTO) {
        return ResponseEntity.ok(membershipPlanService.updatePlan(id, requestDTO));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @SecurityRequirement(name = "bearerAuth")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> removeMembershipPlan(@PathVariable UUID id) {
        membershipPlanService.deletePlan(id);
        return ResponseEntity.noContent().build();
    }
}
