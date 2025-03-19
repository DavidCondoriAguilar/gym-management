package com.gymmanagement.gym_app.controller;

import com.gymmanagement.gym_app.model.MembershipPlanModel;
import com.gymmanagement.gym_app.service.MembershipPlanService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/membership-plans")
@RequiredArgsConstructor
public class MembershipPlanController {

    private final MembershipPlanService membershipPlanService;

    @PostMapping
    public ResponseEntity<MembershipPlanModel> createMembershipPlan(@Valid @RequestBody MembershipPlanModel membershipPlanModel) {
        return ResponseEntity.ok(membershipPlanService.createMembershipPlan(membershipPlanModel));
    }

    @GetMapping("/{id}")
    public ResponseEntity<MembershipPlanModel> getMembershipPlanById(@PathVariable UUID id) {
        return ResponseEntity.ok(membershipPlanService.getMembershipPlanById(id));
    }

    @GetMapping
    public ResponseEntity<List<MembershipPlanModel>> listMembershipPlans() {
        return ResponseEntity.ok(membershipPlanService.getAllMembershipPlans());
    }

    @PutMapping("/{id}")
    public ResponseEntity<MembershipPlanModel> updateMembershipPlan(@PathVariable UUID id, @Valid @RequestBody MembershipPlanModel membershipPlanModel) {
        return ResponseEntity.ok(membershipPlanService.updateMembershipPlan(id, membershipPlanModel));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> removeMembershipPlan(@PathVariable UUID id) {
        membershipPlanService.deleteMembershipPlan(id);
        return ResponseEntity.noContent().build();
    }
}
