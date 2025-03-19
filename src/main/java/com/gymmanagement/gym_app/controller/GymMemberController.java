package com.gymmanagement.gym_app.controller;

import com.gymmanagement.gym_app.model.GymMemberModel;
import com.gymmanagement.gym_app.service.GymMemberService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("gym-members")
@RequiredArgsConstructor
public class GymMemberController {

    private final GymMemberService gymMemberService;

    @GetMapping
    public ResponseEntity<List<GymMemberModel>> getAllMembers() {
        return ResponseEntity.ok(gymMemberService.getAllMembers());
    }

    @GetMapping("/{id}")
    public ResponseEntity<GymMemberModel> getMemberById(@PathVariable UUID id) {
        return ResponseEntity.ok(gymMemberService.getMemberById(id));
    }

    @PostMapping
    public ResponseEntity<GymMemberModel> createMember(@Valid @RequestBody GymMemberModel gymMemberModel) {
        GymMemberModel createdMember = gymMemberService.createMember(gymMemberModel);
        return ResponseEntity.ok(createdMember);
    }

    @PutMapping("/{id}")
    public ResponseEntity<GymMemberModel> updateMember(@PathVariable UUID id, @Valid @RequestBody GymMemberModel gymMemberModel) {
        GymMemberModel updatedMember = gymMemberService.updateMember(id, gymMemberModel);
        return ResponseEntity.ok(updatedMember);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMember(@PathVariable UUID id) {
        gymMemberService.deleteMember(id);
        return ResponseEntity.noContent().build();
    }
}
