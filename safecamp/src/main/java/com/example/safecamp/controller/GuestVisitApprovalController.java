package com.example.safecamp.controller;

import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.safecamp.dto.GuestVisitResponse;
import com.example.safecamp.security.UserPrincipal;
import com.example.safecamp.service.GuestVisitApprovalService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/guest-visits")
@RequiredArgsConstructor
public class GuestVisitApprovalController {

    private final GuestVisitApprovalService approvalService;

    @PreAuthorize("hasRole('SECURITY')")
    @PostMapping("/{visitId}/approve")
    public ResponseEntity<GuestVisitResponse> approveGuestVisit(
            @PathVariable UUID visitId,
            @AuthenticationPrincipal UserPrincipal approver) {
        GuestVisitResponse response = approvalService.approveVisit(visitId, approver.getId());

       return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PreAuthorize("hasRole('SECURITY')")
    @PostMapping("/{visitId}/reject")
    public ResponseEntity<GuestVisitResponse> rejectGuestVisit(
            @PathVariable UUID visitId,
            @AuthenticationPrincipal UserPrincipal approver) {
        GuestVisitResponse response = approvalService.rejectVisit(visitId, approver.getId());

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

}
