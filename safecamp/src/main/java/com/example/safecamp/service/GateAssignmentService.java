package com.example.safecamp.service;

import java.time.LocalDateTime;
import java.util.UUID;

import com.example.safecamp.dto.GateAssignmentResponse;

public interface GateAssignmentService {
    GateAssignmentResponse assignGuardToGate(
            UUID guardId,
            UUID gateId,
            LocalDateTime startTime,
            LocalDateTime endTime,
            UUID adminId);

    void endAssignment(
            UUID assignmentId,
            UUID adminId);
}
