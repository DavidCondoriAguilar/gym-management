package com.gymmanagement.gym_app.controller;

import com.gymmanagement.gym_app.dto.request.GymMemberRequestDTO;
import com.gymmanagement.gym_app.dto.response.GymMemberResponseDTO;
import com.gymmanagement.gym_app.service.GymMemberService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("gym-members")
@RequiredArgsConstructor
public class GymMemberController {

    private final GymMemberService gymMemberService;

    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    @GetMapping
    public ResponseEntity<List<GymMemberResponseDTO>> getAllMembers() {
        return ResponseEntity.ok(gymMemberService.getAllMembers());
    }

    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    @GetMapping("/{id}")
    public ResponseEntity<GymMemberResponseDTO> getMemberById(@PathVariable UUID id) {
        return ResponseEntity.ok(gymMemberService.getMemberById(id));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<GymMemberResponseDTO> createMember(@Valid @RequestBody GymMemberRequestDTO gymMemberRequestDTO) {
        GymMemberResponseDTO createdMember = gymMemberService.createMember(gymMemberRequestDTO);
        return ResponseEntity.ok(createdMember);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<GymMemberResponseDTO> updateMember(@PathVariable UUID id, @Valid @RequestBody GymMemberRequestDTO gymMemberRequestDTO) {
        GymMemberResponseDTO updatedMember = gymMemberService.updateMember(id, gymMemberRequestDTO);
        return ResponseEntity.ok(updatedMember);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMember(@PathVariable UUID id) {
        gymMemberService.deleteMember(id);
        return ResponseEntity.noContent().build();
    }
}
