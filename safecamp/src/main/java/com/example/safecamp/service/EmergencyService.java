package com.example.safecamp.service;

import java.util.UUID;

import com.example.safecamp.dto.CreateEmergencyAlertRequest;
import com.example.safecamp.dto.EmergencyAlertResponse;

public interface EmergencyService {

    EmergencyAlertResponse createEmergency(
            CreateEmergencyAlertRequest request,
            UUID userId);

    EmergencyAlertResponse acknowledgeEmergency(
            UUID emergencyId,
            UUID guardId);

    EmergencyAlertResponse resolveEmergency(
            UUID emergencyId,
            UUID guardId);
}
