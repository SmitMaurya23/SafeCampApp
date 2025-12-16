package com.example.safecamp.controller;

import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.safecamp.dto.GuestVisitResponse;
import com.example.safecamp.service.GuestVisitApprovalService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/guest-visits")
@RequiredArgsConstructor
public class GuestVisitApprovalController {

    private final GuestVisitApprovalService approvalService;

    @PostMapping("/{visitId}/approve")
    public ResponseEntity<GuestVisitResponse> approveGuestVisit(
            @PathVariable UUID visitId,
            @RequestParam UUID approverId // TEMP until auth
    ) {
        GuestVisitResponse response = approvalService.approveVisit(visitId, approverId);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/{visitId}/reject")
    public ResponseEntity<GuestVisitResponse> rejectGuestVisit(
            @PathVariable UUID visitId,
            @RequestParam UUID approverId // TEMP until auth
    ) {
        GuestVisitResponse response = approvalService.rejectVisit(visitId, approverId);

        return ResponseEntity.ok(response);
    }

}
