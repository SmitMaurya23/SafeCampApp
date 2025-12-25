package com.example.safecamp.serviceImpl;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.safecamp.dto.CreateEmergencyAlertRequest;
import com.example.safecamp.dto.EmergencyAlertResponse;
import com.example.safecamp.entity.EmergencyAlert;
import com.example.safecamp.entity.User;
import com.example.safecamp.enums.EmergencyStatus;
import com.example.safecamp.enums.Role;
import com.example.safecamp.repository.EmergencyAlertRepository;
import com.example.safecamp.repository.UserRepository;
import com.example.safecamp.service.EmergencyService;

import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class EmergencyServiceImpl implements EmergencyService {

    private final EmergencyAlertRepository emergencyAlertRepository;
    private final UserRepository userRepository;

    @Override
    public EmergencyAlertResponse createEmergency(
            CreateEmergencyAlertRequest request,
            UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        // Guards cannot trigger emergencies
        if (user.getRole() == Role.SECURITY) {
            throw new IllegalStateException("Security cannot raise an emergency");
        }

        // Prevent multiple active emergencies
        boolean alreadyActive = emergencyAlertRepository
                .existsByUserAndStatus(user, EmergencyStatus.ACTIVE);

        if (alreadyActive) {
            throw new IllegalStateException("An active emergency already exists");
        }

        EmergencyAlert alert = EmergencyAlert.builder()
                .user(user)
                .latitude(request.getLatitude())
                .longitude(request.getLongitude())
                .message(request.getMessage())
                .status(EmergencyStatus.ACTIVE)
                .build();

        EmergencyAlert saved = emergencyAlertRepository.save(alert);

        return mapToResponse(saved);
    }

    @Override
    public EmergencyAlertResponse acknowledgeEmergency(
            UUID emergencyId,
            UUID guardId) {
        EmergencyAlert alert = emergencyAlertRepository.findById(emergencyId)
                .orElseThrow(() -> new IllegalArgumentException("Emergency not found"));

        if (alert.getStatus() == EmergencyStatus.RESOLVED) {
            throw new IllegalStateException("Emergency already resolved");
        }

        User guard = userRepository.findById(guardId)
                .orElseThrow(() -> new IllegalArgumentException("Guard not found"));

        if (guard.getRole() != Role.SECURITY) {
            throw new IllegalStateException("Only security can acknowledge emergencies");
        }

        alert.setStatus(EmergencyStatus.ACKNOWLEDGED);

        EmergencyAlert updated = emergencyAlertRepository.save(alert);

        return mapToResponse(updated);
    }

    @Override
    public EmergencyAlertResponse resolveEmergency(
            UUID emergencyId,
            UUID guardId) {
        EmergencyAlert alert = emergencyAlertRepository.findById(emergencyId)
                .orElseThrow(() -> new IllegalArgumentException("Emergency not found"));

        if (alert.getStatus() == EmergencyStatus.RESOLVED) {
            throw new IllegalStateException("Emergency already resolved");
        }

        User guard = userRepository.findById(guardId)
                .orElseThrow(() -> new IllegalArgumentException("Guard not found"));

        if (guard.getRole() != Role.SECURITY) {
            throw new IllegalStateException("Only security can resolve emergencies");
        }

        alert.setStatus(EmergencyStatus.RESOLVED);
        alert.setResolvedAt(LocalDateTime.now());

        EmergencyAlert updated = emergencyAlertRepository.save(alert);

        return mapToResponse(updated);
    }

    private EmergencyAlertResponse mapToResponse(EmergencyAlert alert) {
        User user = alert.getUser();

        return EmergencyAlertResponse.builder()
                .alertId(alert.getId())
                .userId(user.getId())
                .userName(user.getName())
                .userPhone(user.getPhone())
                .latitude(alert.getLatitude())
                .longitude(alert.getLongitude())
                .status(alert.getStatus())
                .createdAt(alert.getCreatedAt())
                .build();
    }
}
