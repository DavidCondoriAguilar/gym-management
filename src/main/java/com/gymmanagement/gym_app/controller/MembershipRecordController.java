package com.gymmanagement.gym_app.controller;

import com.gymmanagement.gym_app.model.MembershipRecordModel;
import com.gymmanagement.gym_app.service.MembershipRecordService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/membership-records")
@RequiredArgsConstructor
public class MembershipRecordController {

    private final MembershipRecordService membershipRecordService;

    @PostMapping
    public ResponseEntity<MembershipRecordModel> createMembershipRecord(@RequestBody MembershipRecordModel record) {
        return ResponseEntity.ok(membershipRecordService.createMembershipRecord(record));
    }

    @GetMapping("/{gymMemberId}")
    public ResponseEntity<List<MembershipRecordModel>> getMembershipRecordsByMember(@PathVariable UUID gymMemberId) {
        return ResponseEntity.ok(membershipRecordService.getMembershipRecordsByMember(gymMemberId));
    }

    @GetMapping
    public ResponseEntity<List<MembershipRecordModel>> getAllMembershipRecords() {
        return ResponseEntity.ok(membershipRecordService.getAllMembershipRecords());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMembershipRecord(@PathVariable UUID id) {
        membershipRecordService.deleteMembershipRecord(id);
        return ResponseEntity.noContent().build();
    }
}
