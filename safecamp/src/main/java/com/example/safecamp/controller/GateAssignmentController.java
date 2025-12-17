package com.example.safecamp.controller;

import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.safecamp.dto.AssignGateRequest;
import com.example.safecamp.dto.GateAssignmentResponse;
import com.example.safecamp.security.UserPrincipal;
import com.example.safecamp.service.GateAssignmentService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/admin/gate-assignments")
@RequiredArgsConstructor
public class GateAssignmentController {

    private final GateAssignmentService gateAssignmentService;

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<GateAssignmentResponse> assignGuardToGate(
            @Valid @RequestBody AssignGateRequest request, @AuthenticationPrincipal UserPrincipal admin) {
        GateAssignmentResponse response = gateAssignmentService.assignGuardToGate(
                request.getGuardId(),
                request.getGateId(),
                request.getStartTime(),
                request.getEndTime(),
                admin.getId());

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/{assignmentId}/end")
    public ResponseEntity<Void> endAssignment(
            @PathVariable UUID assignmentId,
           @AuthenticationPrincipal UserPrincipal admin
    ) {
        gateAssignmentService.endAssignment(assignmentId, admin.getId());
        return ResponseEntity.noContent().build();
    }

}
