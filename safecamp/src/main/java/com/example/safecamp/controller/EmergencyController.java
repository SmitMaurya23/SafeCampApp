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

import com.example.safecamp.dto.CreateEmergencyAlertRequest;
import com.example.safecamp.dto.EmergencyAlertResponse;
import com.example.safecamp.security.UserPrincipal;
import com.example.safecamp.service.EmergencyService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/emergencies")
@RequiredArgsConstructor
public class EmergencyController {

    private final EmergencyService emergencyService;

    @PostMapping
    public ResponseEntity<EmergencyAlertResponse> createEmergency(
            @Valid @RequestBody CreateEmergencyAlertRequest request,
            @AuthenticationPrincipal UserPrincipal user) {
        EmergencyAlertResponse response = emergencyService.createEmergency(request, user.getId());

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @PostMapping("/{emergencyId}/acknowledge")
    @PreAuthorize("hasRole('SECURITY')")
    public ResponseEntity<EmergencyAlertResponse> acknowledgeEmergency(
            @PathVariable UUID emergencyId,
            @AuthenticationPrincipal UserPrincipal guard) {
        EmergencyAlertResponse response = emergencyService.acknowledgeEmergency(emergencyId, guard.getId());

        return ResponseEntity.ok(response);
    }

    @PostMapping("/{emergencyId}/resolve")
    @PreAuthorize("hasRole('SECURITY')")
    public ResponseEntity<EmergencyAlertResponse> resolveEmergency(
            @PathVariable UUID emergencyId,
            @AuthenticationPrincipal UserPrincipal guard) {
        EmergencyAlertResponse response = emergencyService.resolveEmergency(emergencyId, guard.getId());

        return ResponseEntity.ok(response);
    }
}
