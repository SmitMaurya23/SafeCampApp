package com.example.safecamp.controller;

import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.safecamp.dto.AssignGateRequest;
import com.example.safecamp.dto.GateAssignmentResponse;
import com.example.safecamp.service.GateAssignmentService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/admin/gate-assignments")
@RequiredArgsConstructor
public class GateAssignmentController {

    private final GateAssignmentService gateAssignmentService;

    @PostMapping
    public ResponseEntity<GateAssignmentResponse> assignGuardToGate(
            @RequestParam UUID adminId, // TEMP until auth
            @Valid @RequestBody AssignGateRequest request) {
        GateAssignmentResponse response = gateAssignmentService.assignGuardToGate(
                request.getGuardId(),
                request.getGateId(),
                request.getStartTime(),
                request.getEndTime(),
                adminId);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/{assignmentId}/end")
    public ResponseEntity<Void> endAssignment(
            @PathVariable UUID assignmentId,
            @RequestParam UUID adminId // TEMP until auth
    ) {
        gateAssignmentService.endAssignment(assignmentId, adminId);
        return ResponseEntity.noContent().build();
    }

}
