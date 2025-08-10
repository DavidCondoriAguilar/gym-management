package com.gymmanagement.gym_app.controller;

import com.gymmanagement.gym_app.dto.request.MembershipRecordRequestDTO;
import com.gymmanagement.gym_app.dto.response.MembershipRecordResponseDTO;
import com.gymmanagement.gym_app.model.MembershipRecordModel;
import com.gymmanagement.gym_app.service.MembershipRecordService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/membership-records")
@RequiredArgsConstructor
public class MembershipRecordController {

    private final MembershipRecordService membershipRecordService;

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<MembershipRecordResponseDTO> createMembershipRecord(@Valid @RequestBody MembershipRecordRequestDTO requestDTO) {
        return ResponseEntity.ok(membershipRecordService.createRecord(requestDTO));
    }

    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    @GetMapping("/{gymMemberId}")
    public ResponseEntity<List<MembershipRecordResponseDTO>> getMembershipRecordsByMember(@PathVariable UUID gymMemberId) {
        return ResponseEntity.ok(membershipRecordService.getRecordsByMember(gymMemberId));
    }

    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    @GetMapping
    public ResponseEntity<List<MembershipRecordResponseDTO>> getAllMembershipRecords() {
        return ResponseEntity.ok(membershipRecordService.getAllRecords());
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMembershipRecord(@PathVariable UUID id) {
        membershipRecordService.deleteRecord(id);
        return ResponseEntity.noContent().build();
    }
}
